package comidev.comistore.components.product;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.product.doc.ProductDoc;
import comidev.comistore.components.product.request.ProductCreate;
import comidev.comistore.components.product.response.ProductDetails;
import comidev.comistore.components.product.util.ProductSearch;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController implements ProductDoc {
    private final ProductService productService;

    @GetMapping
    @ResponseBody
    public List<ProductDetails> getAllProductsOrFields(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category) {
        ProductSearch productSearch = new ProductSearch(name, category);
        return productService.getAllProductsOrFields(productSearch);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ProductDetails getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ProductDetails registerProduct(@Valid @RequestBody ProductCreate productReq) {
        return productService.save(productReq);
    }

}
