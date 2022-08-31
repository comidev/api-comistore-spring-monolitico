package comidev.comistore.components.invoice.doc;

import java.util.List;

import comidev.comistore.components.invoice.request.*;
import comidev.comistore.components.invoice.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface InvoiceDoc {
    @Operation(summary = " Devuelve lista de todas las compras", security = @SecurityRequirement(name = "bearer-key"))
    List<InvoiceDetails> getAllInvoices();

    @Operation(summary = "Devuelve las compras de un cliente por su id", security = @SecurityRequirement(name = "bearer-key"))
    List<InvoiceDetails> getInvoiceByCustomer(Long id);

    @Operation(summary = "Registra una compra de un cliente", security = @SecurityRequirement(name = "bearer-key"))
    void registerInvoice(InvoiceCreate body);
}
