package comidev.comistore.components.category.request;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreate {
    @NotEmpty(message = "No puede ser vacío")
    private String name;
}
