package edu.universidad.pop.modules.recuperacion;

import edu.universidad.pop.common.enums.EstadoObjeto;
import edu.universidad.pop.common.enums.EstadoSolicitud;
import edu.universidad.pop.common.exception.BusinessRuleException;
import edu.universidad.pop.modules.objeto.domain.Objeto;
import edu.universidad.pop.modules.objeto.repository.ObjetoRepository;
import edu.universidad.pop.modules.recuperacion.domain.SolicitudRecuperacion;
import edu.universidad.pop.modules.recuperacion.dto.RegistrarEntregaRequest;
import edu.universidad.pop.modules.recuperacion.repository.EntregaRepository;
import edu.universidad.pop.modules.recuperacion.repository.SolicitudRecuperacionRepository;
import edu.universidad.pop.modules.recuperacion.service.impl.EntregaServiceImpl;
import edu.universidad.pop.modules.usuario.domain.Usuario;
import edu.universidad.pop.modules.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntregaServiceTest {

    @Mock private EntregaRepository entregaRepository;
    @Mock private SolicitudRecuperacionRepository solicitudRepository;
    @Mock private ObjetoRepository objetoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private EntregaServiceImpl entregaService;

    /**
     * Regla #6: solo se entrega si la solicitud fue aprobada.
     */
    @Test
    void registrarEntrega_FallaSiSolicitudNoEstaAprobada() {
        SolicitudRecuperacion solicitud = new SolicitudRecuperacion();
        solicitud.setEstado(EstadoSolicitud.EN_REVISION);
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        RegistrarEntregaRequest req = new RegistrarEntregaRequest(
                1L,
                true,
                "firma",
                "observaciones"
        );

        assertThrows(BusinessRuleException.class,
                () -> entregaService.registrar(req, "encargado@universidad.edu"));
    }

    /**
     * Regla #7: no se registra entrega sin confirmación de recepción.
     */
    @Test
    void registrarEntrega_FallaSinConfirmacion() {
        SolicitudRecuperacion solicitud = new SolicitudRecuperacion();
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        RegistrarEntregaRequest req = new RegistrarEntregaRequest(
                1L,
                false,
                "firma",
                "observaciones"
        );

        assertThrows(BusinessRuleException.class,
                () -> entregaService.registrar(req, "encargado@universidad.edu"));
    }

    /**
     * Regla #9: el objeto debe estar EN_PROCESO_DE_RECUPERACION.
     */
    @Test
    void registrarEntrega_FallaSiObjetoNoEstaEnProceso() {
        Objeto objeto = new Objeto();
        objeto.setId(10L);
        objeto.setEstado(EstadoObjeto.DISPONIBLE_PARA_RECLAMACION);

        SolicitudRecuperacion solicitud = new SolicitudRecuperacion();
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setObjeto(objeto);

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(entregaRepository.existsByObjetoId(10L)).thenReturn(false);

        Usuario encargado = new Usuario();
        encargado.setCorreoInstitucional("encargado@universidad.edu");
        when(usuarioRepository.findByCorreoInstitucional("encargado@universidad.edu"))
                .thenReturn(Optional.of(encargado));

        RegistrarEntregaRequest req = new RegistrarEntregaRequest(
                1L,
                true,
                "firma",
                "observaciones"
        );

        assertThrows(BusinessRuleException.class,
                () -> entregaService.registrar(req, "encargado@universidad.edu"));
    }
}