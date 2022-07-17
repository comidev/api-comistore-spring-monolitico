package comidev.comistore.components.product;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.product.dto.ProductReq;
import comidev.comistore.components.product.dto.ProductRes;
import comidev.comistore.components.product.dto.ProductSearch;
import comidev.comistore.utils.Validator;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "findAllOrFields - Devuelve lista de productos, por nombre y/o categoria", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRes.class))),
            @ApiResponse(responseCode = "204", description = "NO CONTENT - No hay productos", content = @Content),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - La categoria no existe", content = @Content),
    })
    @GetMapping
    public ResponseEntity<List<ProductRes>> findAllOrFields(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category) {
        ProductSearch productSearch = new ProductSearch(name, category);

        List<ProductRes> products = productService.findAllOrFields(productSearch);

        return ResponseEntity.status(products.isEmpty() ? 204 : 200).body(products);
    }

    @Operation(summary = "findById - Devuelve producto por id", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRes.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - No se encuentra el producto", content = @Content),
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ProductRes findById(@PathVariable Long id) {

        ProductRes product = productService.findById(id);

        return product;
    }

    @Operation(summary = "save - Registra un producto", responses = {
            @ApiResponse(responseCode = "201", description = "CREATED - Se registra el producto", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRes.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - La categoria no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - Error de validacion", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Se requiere Token con Rol(es): ADMIN", content = @Content),
    }, security = @SecurityRequirement(name = "bearer-key"))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ProductRes save(@Valid @RequestBody ProductReq productReq,
            BindingResult bindingResult) {

        Validator.checkOrThrowBadRequest(bindingResult);

        ProductRes productRes = productService.save(productReq);

        return productRes;
    }

}
