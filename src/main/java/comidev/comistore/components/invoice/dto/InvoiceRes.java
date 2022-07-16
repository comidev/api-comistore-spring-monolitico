package comidev.comistore.components.invoice.dto;

import java.util.List;

import comidev.comistore.components.invoice.Invoice;
import comidev.comistore.components.invoice_item.dto.InvoiceItemRes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceRes {
    private String description;
    private Float total;
    private List<InvoiceItemRes> items;

    public InvoiceRes(Invoice invoice) {
        this.description = invoice.getDescription();
        this.total = invoice.getTotal();
        this.items = invoice.getInvoiceItems().stream()
                .map(InvoiceItemRes::new).toList();
    }
}
