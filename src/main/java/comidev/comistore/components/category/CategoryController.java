package comidev.comistore.components.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryRepo categoryRepo;

    @GetMapping()
    public ResponseEntity<List<Category>> findAll() {
        List<Category> categories = categoryRepo.findAll();
        return ResponseEntity.status(categories.isEmpty()
                ? HttpStatus.NO_CONTENT
                : HttpStatus.OK)
                .body(categories);
    }
}
