package comidev.comistore.components.invoice_item.dto;

import comidev.comistore.components.invoice_item.InvoiceItem;
import comidev.comistore.components.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InvoiceItemRes {
    private Integer quantity;
    private Float price;
    private String name;
    private String photoUrl;
    private String description;

    public InvoiceItemRes(InvoiceItem invoiceItem) {
        this.quantity = invoiceItem.getQuantity();
        Product product = invoiceItem.getProduct();
        this.price = product.getPrice();
        this.name = product.getName();
        this.photoUrl = product.getPhotoUrl();
        this.description = product.getDescription();
    }
}
