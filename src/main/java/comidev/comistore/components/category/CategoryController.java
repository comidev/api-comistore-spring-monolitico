package comidev.comistore.components.category;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.category.doc.CategoryDoc;
import comidev.comistore.components.category.request.CategoryCreate;
import comidev.comistore.components.category.response.CategoryDetails;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController implements CategoryDoc {
    private final CategoryService service;

    @GetMapping
    @ResponseBody
    public List<CategoryDetails> getAllCategories() {
        return service.getAllCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CategoryDetails registerCategory(
            @Valid @RequestBody CategoryCreate body) {
        return service.registerCategory(body);
    }
}
