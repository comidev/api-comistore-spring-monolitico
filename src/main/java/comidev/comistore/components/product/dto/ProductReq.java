package comidev.comistore.components.product.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReq {
    @NotEmpty(message = "No puede ser vacio")
    private String name;
    @NotEmpty(message = "No puede ser vacio")
    private String photoUrl;
    @NotEmpty(message = "No puede ser vacio")
    private String description;
    @Positive(message = "No puede ser igual o menor a 0")
    private Integer stock;
    @Positive(message = "No puede ser igual o menor a 0")
    private Float price;
    @NotEmpty(message = "No puede ser vacio")
    private List<String> categories;
}
