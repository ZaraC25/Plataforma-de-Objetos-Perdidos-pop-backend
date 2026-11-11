package edu.universidad.pop.modules.auditoria.repository;
import edu.universidad.pop.modules.auditoria.domain.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {}
