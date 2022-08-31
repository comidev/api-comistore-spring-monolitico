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
import comidev.comistore.components.product.request.ProductCreate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
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
    @JoinTable(name = "product_category", joinColumns = {
            @JoinColumn(referencedColumnName = "id", name = "product_id")
    }, inverseJoinColumns = {
            @JoinColumn(referencedColumnName = "id", name = "category_id")
    })
    private Set<Category> categories;

    public Product(ProductCreate dto, Set<Category> categories) {
        this.name = dto.getName();
        this.photoUrl = dto.getPhotoUrl();
        this.description = dto.getDescription();
        this.stock = dto.getStock();
        this.price = dto.getPrice();
        this.categories = categories != null ? categories : new HashSet<>();
    }

    public void addStock(Integer stock) {
        this.stock += stock;
    }
}
