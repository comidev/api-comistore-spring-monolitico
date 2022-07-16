package comidev.comistore.components.invoice_item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import comidev.comistore.components.invoice_item.dto.InvoiceItemReq;
import comidev.comistore.components.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invoice_items")
@NoArgsConstructor
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer quantity;
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public InvoiceItem(Integer quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
    }

    public InvoiceItem(InvoiceItemReq invoiceItemReq) {
        this.quantity = invoiceItemReq.getQuantity();
    }
}
