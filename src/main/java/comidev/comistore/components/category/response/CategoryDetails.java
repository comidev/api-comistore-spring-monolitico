package comidev.comistore.components.category.response;

import comidev.comistore.components.category.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryDetails {
    private Long id;
    private String name;

    public CategoryDetails(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
