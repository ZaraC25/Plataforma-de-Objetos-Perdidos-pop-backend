package edu.universidad.pop.modules.reporte.repository;

import edu.universidad.pop.modules.reporte.domain.ReportePerdida;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportePerdidaRepository extends JpaRepository<ReportePerdida, Long> {

    // Carga también el usuario: open-in-view está desactivado y el acceso lazy fallaría fuera de la transacción
    @EntityGraph(attributePaths = "usuarioReporta")
    List<ReportePerdida> findByUsuarioReporta_Id(Long usuarioId);

    @Query("select r from ReportePerdida r join fetch r.usuarioReporta")
    List<ReportePerdida> findAllConUsuario();
}
