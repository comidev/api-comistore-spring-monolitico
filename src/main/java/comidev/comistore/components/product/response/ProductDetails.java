package comidev.comistore.components.product.response;

import comidev.comistore.components.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDetails {
    private Long id;
    private String name;
    private String photoUrl;
    private String description;
    private Float price;

    public ProductDetails(Product entity) {
        this.id = entity.getId();
        this.price = entity.getPrice();
        this.name = entity.getName();
        this.photoUrl = entity.getPhotoUrl();
        this.description = entity.getDescription();
    }
}
