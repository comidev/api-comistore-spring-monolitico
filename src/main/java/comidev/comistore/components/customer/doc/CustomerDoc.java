package comidev.comistore.components.customer.doc;

import java.util.List;

import comidev.comistore.components.customer.request.*;
import comidev.comistore.components.customer.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface CustomerDoc {
    @Operation(summary = "Devuelve lista de clientes", security = @SecurityRequirement(name = "bearer-key"))
    List<CustomerDetails> getAllCustomers();

    @Operation(summary = "Devuelve cliente por id", security = @SecurityRequirement(name = "bearer-key"))
    CustomerDetails getCustomerById(Long id);

    @Operation(summary = "Registra un cliente")
    CustomerDetails registerCustomer(CustomerCreate customerReq);

    @Operation(summary = "Actualiza cliente por id", security = @SecurityRequirement(name = "bearer-key"))
    CustomerDetails updateCustomer(Long id, CustomerUpdate customerReq);

    @Operation(summary = "elimina cliente por id", security = @SecurityRequirement(name = "bearer-key"))
    void deleteCustomer(Long id, String password);

    @Operation(summary = "verifica si el email ya está registrado")
    boolean existsEmail(String email);
}
