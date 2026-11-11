package edu.universidad.pop.modules.reporte.repository;

import edu.universidad.pop.modules.reporte.domain.ReportePerdida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportePerdidaRepository extends JpaRepository<ReportePerdida, Long> {
}