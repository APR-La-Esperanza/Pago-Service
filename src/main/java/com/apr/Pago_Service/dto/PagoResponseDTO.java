package com.apr.Pago_Service.dto;

import com.apr.Pago_Service.model.MedioPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoResponseDTO {

    private Long id;
    private Long facturaId;
    private Long socioId;
    private BigDecimal montoPagado;
    private LocalDateTime fechaPago;
    private MedioPago medioPago;
    private String comprobante;

    public PagoResponseDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFacturaId() { return facturaId; }
    public void setFacturaId(Long facturaId) { this.facturaId = facturaId; }
    public Long getSocioId() { return socioId; }
    public void setSocioId(Long socioId) { this.socioId = socioId; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    public MedioPago getMedioPago() { return medioPago; }
    public void setMedioPago(MedioPago medioPago) { this.medioPago = medioPago; }
    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }
}
