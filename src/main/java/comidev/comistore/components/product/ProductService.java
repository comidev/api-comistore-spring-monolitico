package comidev.comistore.components.product;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import comidev.comistore.components.category.Category;
import comidev.comistore.components.category.CategoryService;
import comidev.comistore.components.product.request.ProductCreate;
import comidev.comistore.components.product.response.ProductDetails;
import comidev.comistore.components.product.util.ProductSearch;
import comidev.comistore.exceptions.HttpException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryService categoryService;

    public List<ProductDetails> getAllProductsOrFields(ProductSearch search) {
        String categorySearch = search.getCategory();
        String name = search.getName();

        List<Product> productsDB = null;

        if (categorySearch != null) {
            Category categoryDB = categoryService.findCategoryByName(categorySearch);
            Predicate<Product> filter = name != null
                    ? p -> p.getCategories().contains(categoryDB)
                            && p.getName().contains(name)
                    : p -> p.getCategories().contains(categoryDB);
            productsDB = productRepo.findAll().stream()
                    .filter(filter)
                    .toList();
        } else if (name != null) {
            productsDB = productRepo.findByNameContaining(name);
        } else {
            productsDB = productRepo.findAll();
        }
        return productsDB.stream()
                .map(ProductDetails::new)
                .collect(Collectors.toList());
    }

    public ProductDetails getProductById(Long id) {
        return new ProductDetails(findById(id));
    }

    public ProductDetails save(ProductCreate body) {
        Set<Category> categories = body.getCategories().stream()
                .map(categoryService::findCategoryByName)
                .collect(Collectors.toSet());
        Product productNew = new Product(body, categories);
        return new ProductDetails(productRepo.save(productNew));
    }

    public Product updateStock(Long id, Integer stock) {
        Product productDB = findById(id);
        productDB.addStock(stock);
        return productRepo.save(productDB);
    }

    private Product findById(Long id) {
        return productRepo.findById(id).orElseThrow(() -> {
            String message = "El producto no existe!!";
            return new HttpException(HttpStatus.NOT_FOUND, message);
        });
    }
}
