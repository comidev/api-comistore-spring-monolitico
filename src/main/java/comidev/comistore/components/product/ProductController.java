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

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductRes>> findAllOrFields(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category) {
        ProductSearch productSearch = new ProductSearch(name, category);

        List<ProductRes> products = productService.findAllOrFields(productSearch);

        return ResponseEntity.status(products.isEmpty() ? 204 : 200).body(products);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ProductRes findById(@PathVariable Long id) {

        ProductRes product = productService.findById(id);

        return product;
    }

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
