package edu.universidad.pop.modules.objeto;

import edu.universidad.pop.common.enums.CategoriaObjeto;
import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.common.exception.BusinessRuleException;
import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.objeto.dto.CrearObjetoRequest;
import edu.universidad.pop.modules.objeto.dto.ObjetoDTO;
import edu.universidad.pop.modules.objeto.mapper.ObjetoMapper;
import edu.universidad.pop.modules.objeto.repository.ObjetoRepository;
import edu.universidad.pop.modules.objeto.service.impl.ObjetoServiceImpl;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class ObjetoServiceTest {

    @Mock private ObjetoRepository objetoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ObjetoMapper objetoMapper;

    @InjectMocks private ObjetoServiceImpl objetoService;

    /**
     * RF-03 — Registro de objeto encontrado con datos válidos.
     */
    @Test
    void registrar_GuardaObjetoYDevuelveDTO() {
        CrearObjetoRequest req = new CrearObjetoRequest(
                "Laptop negra",
                "Laptop negra con sticker rojo",
                CategoriaObjeto.ELECTRONICA,
                LocalDateTime.now().minusDays(1),
                "Bloque B, piso 2"
        );

        Usuario registrador = new Usuario();
        registrador.setId(1L);
        registrador.setCorreoInstitucional("encargado@universidad.edu");

        Objeto objeto = new Objeto();
        objeto.setId(1L);
        objeto.setTitulo(req.titulo());
        objeto.setDescripcion(req.descripcion());
        objeto.setCategoria(req.categoria());
        objeto.setFechaHallazgo(req.fechaHallazgo());
        objeto.setUbicacion(req.ubicacion());

        when(usuarioRepository.findByCorreoInstitucional("encargado@universidad.edu"))
                .thenReturn(Optional.of(registrador));
        when(objetoMapper.toEntity(req)).thenReturn(objeto);
        when(objetoRepository.save(any(Objeto.class))).thenReturn(objeto);
        when(objetoMapper.toDTO(any(Objeto.class)))
                .thenAnswer(inv -> ObjetoDTO.from(inv.getArgument(0)));

        ObjetoDTO resultado = objetoService.registrar(req, "encargado@universidad.edu");

        assertEquals(EstadoObjeto.DISPONIBLE_PARA_RECLAMACION, resultado.estado());
    }

    /**
     * Regla #9 — Transición válida: EN_PROCESO_DE_RECUPERACION → ENTREGADO.
     */
    @Test
    void cambiarEstado_DeEnProcesoAEntregado_EsValido() {
        Objeto objeto = new Objeto();
        objeto.setId(1L);
        objeto.setEstado(EstadoObjeto.EN_PROCESO_DE_RECUPERACION);

        when(objetoRepository.findById(1L)).thenReturn(Optional.of(objeto));
        when(objetoRepository.save(any(Objeto.class))).thenReturn(objeto);
        when(objetoMapper.toDTO(any(Objeto.class)))
                .thenAnswer(inv -> ObjetoDTO.from(inv.getArgument(0)));

        ObjetoDTO resultado = objetoService.cambiarEstado(1L, EstadoObjeto.ENTREGADO);

        assertEquals(EstadoObjeto.ENTREGADO, resultado.estado());
    }

    /**
     * Regla #7 — No se puede marcar ENTREGADO si el objeto no está EN_PROCESO.
     */
    @Test
    void cambiarEstado_AEntregadoDesdeDisponible_Falla() {
        Objeto objeto = new Objeto();
        objeto.setId(1L);
        objeto.setEstado(EstadoObjeto.DISPONIBLE_PARA_RECLAMACION);

        when(objetoRepository.findById(1L)).thenReturn(Optional.of(objeto));

        assertThrows(BusinessRuleException.class,
                () -> objetoService.cambiarEstado(1L, EstadoObjeto.ENTREGADO));
    }

    /**
     * Regla #9 — Un objeto ENTREGADO no puede cambiar de estado.
     */
    @Test
    void cambiarEstado_DesdeEntregado_Falla() {
        Objeto objeto = new Objeto();
        objeto.setId(1L);
        objeto.setEstado(EstadoObjeto.ENTREGADO);

        when(objetoRepository.findById(1L)).thenReturn(Optional.of(objeto));

        assertThrows(BusinessRuleException.class,
                () -> objetoService.cambiarEstado(1L, EstadoObjeto.CERRADO));
    }
}