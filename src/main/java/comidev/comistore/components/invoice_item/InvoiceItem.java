package comidev.comistore.components.invoice_item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import comidev.comistore.components.invoice_item.request.InvoiceItemCreate;
import comidev.comistore.components.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invoice_items")
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public InvoiceItem(InvoiceItemCreate invoiceItemReq, Product product) {
        this.quantity = invoiceItemReq.getQuantity();
        this.product = product;
    }
}
