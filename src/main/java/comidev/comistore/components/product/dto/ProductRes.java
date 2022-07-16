package comidev.comistore.components.product.dto;

import comidev.comistore.components.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRes {
    private Long id;
    private String name;
    private String photoUrl;
    private String description;
    private Float price;

    public ProductRes(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.photoUrl = product.getPhotoUrl();
        this.description = product.getDescription();
        this.price = product.getPrice();
    }
}
