package com.apr.Pago_Service.controller;

import com.apr.Pago_Service.dto.PagoDTO;
import com.apr.Pago_Service.dto.PagoResponseDTO;
import com.apr.Pago_Service.service.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@Tag(name = "Pagos", description = "Endpoints para la gestión, registro e histórico de pagos de facturas de agua potable.")
@SecurityRequirement(name = "bearerAuth")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar pagos", description = "Retorna una lista de pagos. Permite filtrar por socioId o facturaId.")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente.")
    public ResponseEntity<List<PagoResponseDTO>> listarTodos(
            @Parameter(description = "ID del socio para filtrar") @RequestParam(required = false) Long socioId,
            @Parameter(description = "ID de la factura para filtrar") @RequestParam(required = false) Long facturaId) {
        if (socioId != null) {
            return ResponseEntity.ok(service.buscarPorSocioId(socioId));
        }
        if (facturaId != null) {
            return ResponseEntity.ok(service.buscarPorFacturaId(facturaId));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pago por ID", description = "Obtiene los detalles de un pago específico.")
    @ApiResponse(responseCode = "200", description = "Pago encontrado y devuelto.")
    @ApiResponse(responseCode = "404", description = "El pago especificado no existe.")
    public ResponseEntity<PagoResponseDTO> buscarPorId(
            @Parameter(description = "ID único del pago", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un pago", description = "Registra un pago de factura. Realiza llamadas de validación a Socio-Service y Facturacion-Service, y actualiza el estado de la factura a 'PAGADA'.")
    @ApiResponse(responseCode = "201", description = "Pago registrado y factura marcada como PAGADA con éxito.")
    @ApiResponse(responseCode = "400", description = "Datos de entrada incorrectos o problemas de validación inter-servicio.")
    public ResponseEntity<PagoResponseDTO> guardar(
            @RequestBody(description = "Datos para el registro del pago", required = true,
                         content = @Content(schema = @Schema(implementation = PagoDTO.class),
                                            examples = @ExampleObject(value = "{\n  \"socioId\": 1,\n  \"facturaId\": 1,\n  \"montoPagado\": 15000.0,\n  \"medioPago\": \"TRANSFERENCIA\",\n  \"comprobante\": \"TRF-001\"\n}")))
            @Valid @org.springframework.web.bind.annotation.RequestBody PagoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pago", description = "Modifica los datos de un pago registrado previamente.")
    @ApiResponse(responseCode = "200", description = "Pago modificado exitosamente.")
    @ApiResponse(responseCode = "400", description = "Datos provistos inválidos.")
    @ApiResponse(responseCode = "404", description = "El pago no existe.")
    public ResponseEntity<PagoResponseDTO> actualizar(
            @Parameter(description = "ID del pago a actualizar", required = true) @PathVariable Long id,
            @RequestBody(description = "Nuevos datos del pago", required = true,
                         content = @Content(schema = @Schema(implementation = PagoDTO.class),
                                            examples = @ExampleObject(value = "{\n  \"socioId\": 1,\n  \"facturaId\": 1,\n  \"montoPagado\": 15000.0,\n  \"medioPago\": \"DEBITO\",\n  \"comprobante\": \"DEB-002\"\n}")))
            @Valid @org.springframework.web.bind.annotation.RequestBody PagoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago", description = "Elimina permanentemente el registro de un pago.")
    @ApiResponse(responseCode = "204", description = "Pago eliminado correctamente.")
    @ApiResponse(responseCode = "404", description = "El pago no existe.")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del pago a eliminar", required = true) @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
