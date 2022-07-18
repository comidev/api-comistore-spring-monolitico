package comidev.comistore.components.invoice;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.invoice.dto.InvoiceReq;
import comidev.comistore.components.invoice.dto.InvoiceRes;
import comidev.comistore.services.validator.Validator;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/invoices")
@AllArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @Operation(summary = "findAll - Devuelve lista de todas las compras", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvoiceRes.class))),
            @ApiResponse(responseCode = "204", description = "NO CONTENT - No hay compras", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Se requiere Token con Rol(es): ADMIN", content = @Content),
    }, security = @SecurityRequirement(name = "bearer-key"))
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceRes>> findAll() {

        List<InvoiceRes> invoices = invoiceService.findAll();

        return ResponseEntity
                .status(invoices.isEmpty() ? 204 : 200)
                .body(invoices);
    }

    @Operation(summary = "findById - Devuelve las compras de un cliente por su id", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvoiceRes.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - No se encuentra el Cliente", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Se requiere Token con Rol(es): CLIENTE", content = @Content),
    }, security = @SecurityRequirement(name = "bearer-key"))
    @GetMapping("/customer/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<InvoiceRes>> findByCustomerId(@PathVariable Long id) {

        List<InvoiceRes> invoices = invoiceService.findByCustomerId(id);

        return ResponseEntity
                .status(invoices.isEmpty() ? 204 : 200)
                .body(invoices);
    }

    @Operation(summary = "save - Registra una compra de un cliente", responses = {
            @ApiResponse(responseCode = "201", description = "CREATED", content = @Content),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - No se encuentra el Cliente o producto", content = @Content),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - Error de validacion", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Se requiere Token con Rol(es): CLIENTE", content = @Content),
    }, security = @SecurityRequirement(name = "bearer-key"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENTE')")
    public void save(@Valid @RequestBody InvoiceReq invoiceReq, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        invoiceService.save(invoiceReq);
    }
}
