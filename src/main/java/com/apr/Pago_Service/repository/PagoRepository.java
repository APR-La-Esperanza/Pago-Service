package com.apr.Pago_Service.repository;

import com.apr.Pago_Service.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByFacturaId(Long facturaId);
    List<Pago> findBySocioId(Long socioId);
}
