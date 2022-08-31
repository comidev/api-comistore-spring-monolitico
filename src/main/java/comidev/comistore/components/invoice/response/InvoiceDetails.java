package comidev.comistore.components.invoice.response;

import java.util.List;

import comidev.comistore.components.invoice.Invoice;
import comidev.comistore.components.invoice_item.response.InvoiceItemDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceDetails {
    private String description;
    private Float total;
    private List<InvoiceItemDetails> items;

    public InvoiceDetails(Invoice invoice) {
        this.description = invoice.getDescription();
        this.total = invoice.getTotal();
        this.items = invoice.getInvoiceItems().stream()
                .map(InvoiceItemDetails::new).toList();
    }
}
