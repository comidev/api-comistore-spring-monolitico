package comidev.comistore.components.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import comidev.comistore.components.category.Category;
import comidev.comistore.components.category.CategoryRepo;
import comidev.comistore.components.product.dto.ProductReq;
import comidev.comistore.components.product.dto.ProductRes;
import comidev.comistore.components.product.dto.ProductSearch;
import comidev.comistore.exceptions.HttpException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private ProductService productService;
    @Mock
    private ProductRepo productRepo;
    @Mock
    private CategoryRepo categoryRepo;

    @BeforeEach
    void beforeEach() {
        this.productService = new ProductService(productRepo, categoryRepo);
    }

    @Test
    void PuedeDarmeContenidoPorNombreYCategoria_findAllOrFields() {
        // Arreglar
        String name = "Producto interesante";
        String namePart = "ducto interes";
        String categoryName = "Categoria extensa";
        ProductSearch productSearch = new ProductSearch(namePart, categoryName);
        Category category = new Category(categoryName);
        Product productNew = new Product(name, "xd", "xd", 1, 1f);
        productNew.getCategories().add(category);
        when(categoryRepo.findByName(categoryName)).thenReturn(Optional.of(category));
        when(productRepo.findAll()).thenReturn(List.of(productNew));

        // Actuar
        List<ProductRes> productRes = productService.findAllOrFields(productSearch);

        // Afirmar
        assertTrue(productRes.get(0).getName().contains(namePart));
        verify(productRepo).findAll();
        verify(categoryRepo).findByName(categoryName);
    }

    @Test
    void PuedeDarmeNotFoundSiNoExiste_findById() {
        // Arreglar
        Long id = 123l;
        when(productRepo.findById(id)).thenReturn(Optional.empty());
        // Actuar

        // Afirmar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            productService.findById(id);
        }).getStatus();

        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(productRepo).findById(id);
    }

    @Test
    void PuedeLanzarErrorSiNoEncuentraCategoria_save() {
        // Arreglar
        String category = "Categoria mas extensa";
        ProductReq productReq = new ProductReq("x", "x", "x", 1, 1f, List.of(category));
        when(categoryRepo.findByName(category)).thenReturn(Optional.empty());
        // Actuar

        // Afirmar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            productService.save(productReq);
        }).getStatus();

        assertEquals(HttpStatus.NOT_FOUND, status);
    }
}
