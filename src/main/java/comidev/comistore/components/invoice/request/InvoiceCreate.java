package comidev.comistore.components.invoice.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import comidev.comistore.components.invoice_item.request.InvoiceItemCreate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreate {
    @NotEmpty(message = "No puede ser vacío")
    private String description;
    @Positive
    private Long customerId;

    @NotNull(message = "No puede ser vacío")
    private List<@Valid InvoiceItemCreate> items;
}
