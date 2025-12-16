package br.com.ifpe.olx_pp1_api.transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByStripeSessionId(String sessionId);
    java.util.List<Pagamento> findAllByStripeSessionId(String sessionId);
    java.util.List<Pagamento> findByCompradorIdOrderByDataCriacaoDesc(Long compradorId);
}
