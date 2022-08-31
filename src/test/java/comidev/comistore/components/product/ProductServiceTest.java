package comidev.comistore.components.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import comidev.comistore.components.category.Category;
import comidev.comistore.components.category.CategoryService;
import comidev.comistore.components.category.request.CategoryCreate;
import comidev.comistore.components.product.request.ProductCreate;
import comidev.comistore.components.product.response.ProductDetails;
import comidev.comistore.components.product.util.ProductSearch;
import comidev.comistore.exceptions.HttpException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    private ProductService productService;
    @Mock
    private ProductRepo productRepo;
    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void beforeEach() {
        this.productService = new ProductService(productRepo, categoryService);
    }

    @Test
    void PuedeDarmeContenidoPorNombreYCategoria_findAllOrFields() {
        // Arreglar
        String name = "Producto interesante";
        String namePart = "ducto interes";
        String categoryName = "Categoria extensa";
        ProductSearch productSearch = new ProductSearch(namePart, categoryName);
        Category category = new Category(new CategoryCreate(categoryName));
        Product productNew = new Product(new ProductCreate(name, "xd",
                "xd", 1, 1f, null), Set.of(category));
        when(categoryService.findCategoryByName(categoryName)).thenReturn(category);
        when(productRepo.findAll()).thenReturn(List.of(productNew));

        // Actuar
        List<ProductDetails> productRes = productService.getAllProductsOrFields(productSearch);

        // Afirmar
        assertTrue(productRes.get(0).getName().contains(namePart));
        verify(productRepo).findAll();
        verify(categoryService).findCategoryByName(categoryName);
    }

    @Test
    void PuedeDarmeNotFoundSiNoExiste_findById() {
        // Arreglar
        Long id = 123l;
        when(productRepo.findById(id)).thenReturn(Optional.empty());
        // Actuar

        // Afirmar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            productService.getProductById(id);
        }).getStatus();

        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(productRepo).findById(id);
    }
}
