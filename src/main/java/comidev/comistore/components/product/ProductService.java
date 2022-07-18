package comidev.comistore.components.product;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import comidev.comistore.components.category.Category;
import comidev.comistore.components.category.CategoryRepo;
import comidev.comistore.components.product.dto.ProductReq;
import comidev.comistore.components.product.dto.ProductRes;
import comidev.comistore.components.product.dto.ProductSearch;
import comidev.comistore.exceptions.HttpException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    public List<ProductRes> findAllOrFields(ProductSearch productSearch) {
        String categorySearch = productSearch.getCategory();
        String name = productSearch.getName();

        List<Product> productsDB = null;

        if (categorySearch != null) {
            Category categoryDB = findCategoryByName(categorySearch);

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

        return productsDB.stream().map(ProductRes::new).toList();
    }

    private Category findCategoryByName(String name) {
        return categoryRepo.findByName(name)
                .orElseThrow(() -> {
                    String message = "La categoria no existe!";
                    return new HttpException(HttpStatus.NOT_FOUND, message);
                });
    }

    public ProductRes findById(Long id) {
        return productRepo.findById(id)
                .map(ProductRes::new)
                .orElseThrow(() -> {
                    String message = "El producto no existe!";
                    return new HttpException(HttpStatus.NOT_FOUND, message);
                });
    }

    public ProductRes save(ProductReq productReq) {
        Product productNew = new Product(productReq);

        productReq.getCategories().forEach(categoryName -> {
            Category categoryDB = findCategoryByName(categoryName);
            productNew.getCategories().add(categoryDB);
        });

        return new ProductRes(productRepo.save(productNew));
    }

    public Product updateStock(Long id, Integer stock) {
        Product productDB = productRepo.findById(id)
                .orElseThrow(() -> {
                    String message = "El producto no existe!!";
                    return new HttpException(HttpStatus.NOT_FOUND, message);
                });
        Integer stockNEW = productDB.getStock() + stock;
        productDB.setStock(stockNEW);
        return productRepo.save(productDB);
    }
}
