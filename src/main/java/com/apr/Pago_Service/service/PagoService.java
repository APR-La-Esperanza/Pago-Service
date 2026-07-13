package com.apr.Pago_Service.service;

import com.apr.Pago_Service.dto.PagoDTO;
import com.apr.Pago_Service.dto.PagoResponseDTO;
import com.apr.Pago_Service.exception.ResourceNotFoundException;
import com.apr.Pago_Service.mapper.PagoMapper;
import com.apr.Pago_Service.model.Pago;
import com.apr.Pago_Service.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PagoService {

    private final PagoRepository repository;
    private final WebClient socioWebClient;
    private final WebClient facturacionWebClient;

    public PagoService(PagoRepository repository, WebClient socioWebClient, WebClient facturacionWebClient) {
        this.repository = repository;
        this.socioWebClient = socioWebClient;
        this.facturacionWebClient = facturacionWebClient;
    }

    public List<PagoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(PagoMapper::toResponseDTO)
                .toList();
    }

    public PagoResponseDTO buscarPorId(Long id) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
        return PagoMapper.toResponseDTO(pago);
    }

    public List<PagoResponseDTO> buscarPorSocioId(Long socioId) {
        return repository.findBySocioId(socioId)
                .stream()
                .map(PagoMapper::toResponseDTO)
                .toList();
    }

    public List<PagoResponseDTO> buscarPorFacturaId(Long facturaId) {
        return repository.findByFacturaId(facturaId)
                .stream()
                .map(PagoMapper::toResponseDTO)
                .toList();
    }

    public PagoResponseDTO guardar(PagoDTO dto) {
        // 1. Validar Socio
        validarSocioEnSocioService(dto.getSocioId());

        // 2. Validar Factura y obtener sus datos
        Map<String, Object> factura = obtenerFacturaDeFacturacionService(dto.getFacturaId());

        // 3. Registrar el Pago
        Pago pago = PagoMapper.toEntity(dto);
        Pago guardado = repository.save(pago);

        // 4. Actualizar el estado de la Factura a PAGADA
        actualizarEstadoFactura(dto.getFacturaId(), factura);

        return PagoMapper.toResponseDTO(guardado);
    }

    public PagoResponseDTO actualizar(Long id, PagoDTO dto) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));

        if (!pago.getSocioId().equals(dto.getSocioId())) {
            validarSocioEnSocioService(dto.getSocioId());
        }
        if (!pago.getFacturaId().equals(dto.getFacturaId())) {
            obtenerFacturaDeFacturacionService(dto.getFacturaId());
        }

        pago.setFacturaId(dto.getFacturaId());
        pago.setSocioId(dto.getSocioId());
        pago.setMontoPagado(dto.getMontoPagado());
        pago.setMedioPago(dto.getMedioPago());
        pago.setComprobante(dto.getComprobante());
        if (dto.getFechaPago() != null) pago.setFechaPago(dto.getFechaPago());

        Pago actualizado = repository.save(pago);
        return PagoMapper.toResponseDTO(actualizado);
    }

    public void eliminar(Long id) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
        repository.delete(pago);
    }

    private void validarSocioEnSocioService(Long socioId) {
        try {
            Boolean existe = socioWebClient.get()
                    .uri("/socios/" + socioId)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode().is2xxSuccessful())
                    .onErrorReturn(false)
                    .block();

            if (existe == null || !existe) {
                throw new IllegalArgumentException("El Socio con ID " + socioId + " no existe.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al comunicarse con Socio-Service: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> obtenerFacturaDeFacturacionService(Long facturaId) {
        try {
            Map<String, Object> factura = facturacionWebClient.get()
                    .uri("/facturas/" + facturaId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (factura == null) {
                throw new IllegalArgumentException("La Factura con ID " + facturaId + " no existe.");
            }
            return factura;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al obtener la factura: " + e.getMessage());
        }
    }

    private void actualizarEstadoFactura(Long facturaId, Map<String, Object> facturaOriginal) {
        try {
            Map<String, Object> requestBody = new HashMap<>(facturaOriginal);
            requestBody.put("estado", "PAGADA");

            facturacionWebClient.put()
                    .uri("/facturas/" + facturaId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            // Loguear error pero no revertir la transacción si se prefiere resiliencia,
            // o lanzar excepción para hacer rollback. En este caso lanzamos excepción.
            throw new IllegalStateException("No se pudo actualizar el estado de la factura: " + e.getMessage());
        }
    }
}
