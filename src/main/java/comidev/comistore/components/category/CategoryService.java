package comidev.comistore.components.category;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import comidev.comistore.components.category.request.CategoryCreate;
import comidev.comistore.components.category.response.CategoryDetails;
import comidev.comistore.exceptions.HttpException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepo repo;

    public List<CategoryDetails> getAllCategories() {
        return repo.findAll().stream()
                .map(CategoryDetails::new)
                .collect(Collectors.toList());
    }

    public CategoryDetails registerCategory(CategoryCreate body) {
        Category create = new Category(body);
        return new CategoryDetails(repo.save(create));
    }

    public Category findCategoryByName(String name) {
        return repo.findByName(name).orElseThrow(() -> {
            String message = "La categoría no existe!";
            return new HttpException(HttpStatus.NOT_FOUND, message);
        });
    }
}
