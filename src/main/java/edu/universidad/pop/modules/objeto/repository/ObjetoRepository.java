package edu.universidad.pop.modules.objeto.repository;

// Se eliminó el import de EstadoObjeto porque ya no se usa
import edu.universidad.pop.modules.objeto.domain.Objeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjetoRepository extends JpaRepository<Objeto, Long> {
    
    @Query(value = "SELECT COUNT(*) FROM objeto WHERE estado = CAST(:estado AS estado_objeto_enum)", nativeQuery = true)
    long countByEstadoNative(@Param("estado") String estado);
}