package comidev.comistore.components.product;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import comidev.comistore.components.category.Category;
import comidev.comistore.components.product.dto.ProductReq;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String photoUrl;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Float price;

    @ManyToMany()
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(referencedColumnName = "id", name = "product_id"), inverseJoinColumns = @JoinColumn(referencedColumnName = "id", name = "category_id"))
    private Set<Category> categories;

    public Product(String name, String photoUrl, String description,
            Integer stock, Float price) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.categories = new HashSet<>();
    }

    public Product(ProductReq productReq) {
        this.name = productReq.getName();
        this.photoUrl = productReq.getPhotoUrl();
        this.description = productReq.getDescription();
        this.stock = productReq.getStock();
        this.price = productReq.getPrice();
        this.categories = new HashSet<>();
    }

    public Product() {
        this.categories = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Product [categories=" + categories + ", description=" + description + ", id=" + id + ", name=" + name
                + ", photoUrl=" + photoUrl + ", price=" + price + ", stock=" + stock + "]";
    }
}
