package comidev.comistore.components.category;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryRepo categoryRepo;

    @Operation(summary = "findAll - Devuelve lista de categorias", responses = {
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "204", description = "NO CONTENT- No hay categorias", content = @Content),
    })
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        List<Category> categories = categoryRepo.findAll();

        return ResponseEntity
                .status(categories.isEmpty() ? 204 : 200)
                .body(categories);
    }
}
