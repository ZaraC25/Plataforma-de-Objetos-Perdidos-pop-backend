package edu.universidad.pop.modules.recuperacion.repository;

// Se eliminó el import de EstadoSolicitud porque ya no se usa
import edu.universidad.pop.modules.recuperacion.domain.SolicitudRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRecuperacionRepository extends JpaRepository<SolicitudRecuperacion, Long> {
    
    @Query(value = "SELECT COUNT(*) FROM solicitud_recuperacion WHERE estado = CAST(:estado AS estado_solicitud_enum)", nativeQuery = true)
    long countByEstadoNative(@Param("estado") String estado);
}