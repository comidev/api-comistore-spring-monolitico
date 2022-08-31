package comidev.comistore.components.invoice_item.response;

import comidev.comistore.components.invoice_item.InvoiceItem;
import comidev.comistore.components.product.response.ProductDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InvoiceItemDetails {
    private Integer quantity;
    private ProductDetails product;

    public InvoiceItemDetails(InvoiceItem invoiceItem) {
        this.quantity = invoiceItem.getQuantity();
        this.product = new ProductDetails(invoiceItem.getProduct());
    }
}
