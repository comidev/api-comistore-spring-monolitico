package comidev.comistore.components.customer;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.customer.dto.CustomerReq;
import comidev.comistore.components.customer.dto.CustomerRes;
import comidev.comistore.components.customer.dto.CustomerUpdate;
import comidev.comistore.components.customer.dto.EmailBody;
import comidev.comistore.utils.Validator;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @Operation(summary = "findAll - Devuelve lista de clientes", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerRes.class))),
            @ApiResponse(responseCode = "204", description = "NO CONTENT - No hay clientes", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Se requiere Token con Rol(es): ADMIN", content = @Content),
    }, security = @SecurityRequirement(name = "bearer-key"))
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerRes>> findAll() {

        List<CustomerRes> customers = customerService.findAll();

        return ResponseEntity
                .status(customers.isEmpty() ? 204 : 200)
                .body(customers);
    }

    @Operation(summary = "findById - Devuelve cliente por id", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerRes.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - No se encuentra el Cliente", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Se requiere Token con Rol(es): CLIENTE", content = @Content),
    }, security = @SecurityRequirement(name = "bearer-key"))
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CustomerRes findById(@PathVariable Long id) {

        CustomerRes customer = customerService.findById(id);
        return customer;
    }

    @Operation(summary = "save - Registra un cliente", responses = {
            @ApiResponse(responseCode = "201", description = "CREATED - Se registra el cliente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerRes.class))),
            @ApiResponse(responseCode = "409", description = "CONFLICT - El email ya existe", content = @Content),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - El pais no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - Error de validacion", content = @Content),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CustomerRes save(@Valid @RequestBody CustomerReq customerReq, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        CustomerRes customer = customerService.save(customerReq);
        return customer;
    }

    @Operation(summary = "update - Actualiza cliente por id", responses = {
            @ApiResponse(responseCode = "200", description = "OK - Se actualiza el cliente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerRes.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - Error de validacion", content = @Content),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - El clienteId o pais no existe", content = @Content),
            @ApiResponse(responseCode = "409", description = "CONFLICT - El email o username ya existe", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Se requiere Token con Rol(es): CLIENTE", content = @Content),
    }, security = @SecurityRequirement(name = "bearer-key"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CustomerRes update(@Valid @RequestBody CustomerUpdate customerReq, BindingResult bindingResult,
            @PathVariable Long id) {
        Validator.checkOrThrowBadRequest(bindingResult);

        CustomerRes customer = customerService.update(customerReq, id);
        return customer;
    }

    @Operation(summary = "deleteById - elimina cliente por id", responses = {
            @ApiResponse(responseCode = "200", description = "OK - Se elimina el cliente", content = @Content),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - El clienteId no existe", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Se requiere Token con Rol(es): CLIENTE", content = @Content),
    }, security = @SecurityRequirement(name = "bearer-key"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        customerService.deleteById(id);
    }

    @Operation(summary = "existsEmail - verifica si el email ya está registrado", responses = {
            @ApiResponse(responseCode = "200", description = "OK - Se actualiza el cliente", content = @Content),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - Error de validacion", content = @Content),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - El email no existe", content = @Content),
    })
    @PostMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public void existsEmail(@Valid @RequestBody EmailBody email, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        customerService.existsEmail(email.getEmail());
    }
}
