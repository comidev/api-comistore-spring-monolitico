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
import comidev.comistore.utils.Validator;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/invoices")
@AllArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceRes>> findAll() {

        List<InvoiceRes> invoices = invoiceService.findAll();

        return ResponseEntity
                .status(invoices.isEmpty() ? 204 : 200)
                .body(invoices);
    }

    @GetMapping("/customer/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<InvoiceRes>> findByCustomerId(@PathVariable Long id) {

        List<InvoiceRes> invoices = invoiceService.findByCustomerId(id);

        return ResponseEntity
                .status(invoices.isEmpty() ? 204 : 200)
                .body(invoices);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENTE')")
    public void save(@Valid @RequestBody InvoiceReq invoiceReq, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        invoiceService.save(invoiceReq);
    }
}
