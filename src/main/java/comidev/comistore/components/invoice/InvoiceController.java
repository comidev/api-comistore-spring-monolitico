package comidev.comistore.components.invoice;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.invoice.doc.InvoiceDoc;
import comidev.comistore.components.invoice.request.InvoiceCreate;
import comidev.comistore.components.invoice.response.InvoiceDetails;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/invoices")
@AllArgsConstructor
public class InvoiceController implements InvoiceDoc {
    private final InvoiceService invoiceService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @ResponseBody
    public List<InvoiceDetails> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseBody
    @GetMapping("/customer/{id}")
    public List<InvoiceDetails> getInvoiceByCustomer(@PathVariable Long id) {
        return invoiceService.getInvoiceByCustomer(id);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerInvoice(@Valid @RequestBody InvoiceCreate body) {
        invoiceService.registerInvoice(body);
    }
}
