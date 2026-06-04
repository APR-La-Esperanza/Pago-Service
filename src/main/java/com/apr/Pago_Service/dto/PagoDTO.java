package com.apr.Pago_Service.dto;

import com.apr.Pago_Service.model.MedioPago;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoDTO {

    @NotNull(message = "El ID de factura es obligatorio")
    private Long facturaId;

    @NotNull(message = "El ID de socio es obligatorio")
    private Long socioId;

    @NotNull(message = "El monto pagado es obligatorio")
    private BigDecimal montoPagado;

    @NotNull(message = "El medio de pago es obligatorio")
    private MedioPago medioPago;

    private String comprobante;
    private LocalDateTime fechaPago;

    public PagoDTO() {
    }

    public Long getFacturaId() { return facturaId; }
    public void setFacturaId(Long facturaId) { this.facturaId = facturaId; }
    public Long getSocioId() { return socioId; }
    public void setSocioId(Long socioId) { this.socioId = socioId; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
    public MedioPago getMedioPago() { return medioPago; }
    public void setMedioPago(MedioPago medioPago) { this.medioPago = medioPago; }
    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
}
