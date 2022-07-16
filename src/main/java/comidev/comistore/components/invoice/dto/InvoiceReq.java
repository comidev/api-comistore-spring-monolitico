package comidev.comistore.components.invoice.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import comidev.comistore.components.invoice_item.dto.InvoiceItemReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceReq {
    @NotEmpty
    private String description;
    @Positive
    private Long customerId;
    private List<@Valid InvoiceItemReq> items;
}
