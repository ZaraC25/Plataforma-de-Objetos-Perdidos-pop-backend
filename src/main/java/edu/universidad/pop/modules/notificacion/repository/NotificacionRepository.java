package edu.universidad.pop.modules.notificacion.repository;

import edu.universidad.pop.modules.notificacion.domain.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    Page<Notificacion> findByUsuarioDestinatarioCorreoInstitucionalAndLeida(
            String correoInstitucional, boolean leida, Pageable pageable);
}