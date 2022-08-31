package comidev.comistore.components.invoice;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.invoice.request.InvoiceCreate;
import comidev.comistore.components.invoice_item.InvoiceItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invoices")
@NoArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Float total;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany
    private List<InvoiceItem> invoiceItems;

    public Invoice(String description, Float total, Customer customer, List<InvoiceItem> invoiceItems) {
        this.description = description;
        this.total = total;
        this.customer = customer;
        this.invoiceItems = invoiceItems;
    }

    public Invoice(InvoiceCreate dto) {
        this.description = dto.getDescription();
    }
}
