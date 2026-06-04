package com.apr.Pago_Service.mapper;

import com.apr.Pago_Service.dto.PagoDTO;
import com.apr.Pago_Service.dto.PagoResponseDTO;
import com.apr.Pago_Service.model.Pago;

public class PagoMapper {

    public static Pago toEntity(PagoDTO dto) {
        if (dto == null) return null;
        Pago pago = new Pago();
        pago.setFacturaId(dto.getFacturaId());
        pago.setSocioId(dto.getSocioId());
        pago.setMontoPagado(dto.getMontoPagado());
        pago.setMedioPago(dto.getMedioPago());
        pago.setComprobante(dto.getComprobante());
        if (dto.getFechaPago() != null) pago.setFechaPago(dto.getFechaPago());
        return pago;
    }

    public static PagoResponseDTO toResponseDTO(Pago pago) {
        if (pago == null) return null;
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(pago.getId());
        dto.setFacturaId(pago.getFacturaId());
        dto.setSocioId(pago.getSocioId());
        dto.setMontoPagado(pago.getMontoPagado());
        dto.setFechaPago(pago.getFechaPago());
        dto.setMedioPago(pago.getMedioPago());
        dto.setComprobante(pago.getComprobante());
        return dto;
    }
}
