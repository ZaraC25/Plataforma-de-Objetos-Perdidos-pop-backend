package edu.universidad.pop.modules.recuperacion.repository;
import edu.universidad.pop.modules.recuperacion.domain.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EntregaRepository extends JpaRepository<Entrega, Long> {
    boolean existsByObjetoId(Long objetoId);
}
