package comidev.comistore.components.category.doc;

import java.util.List;

import comidev.comistore.components.category.request.CategoryCreate;
import comidev.comistore.components.category.response.CategoryDetails;
import io.swagger.v3.oas.annotations.Operation;

public interface CategoryDoc {
    @Operation(summary = "Devuelve lista de categorías")
    public List<CategoryDetails> getAllCategories();

    @Operation(summary = "Registra las categorías")
    public CategoryDetails registerCategory(CategoryCreate body);
}
