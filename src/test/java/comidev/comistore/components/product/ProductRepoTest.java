package comidev.comistore.components.product;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import comidev.comistore.config.RepoUnitTest;

@RepoUnitTest
public class ProductRepoTest {

    @Autowired
    private ProductRepo productRepo;

    @Test
    void PuedeBuscarPorElContenidoDelNombre_findByNameContaining() {
        String name = "name";
        String namePart = "nam";
        productRepo.save(new Product(name, "photoUrl", "description", 1, 1f));

        List<Product> productsDB = productRepo.findByNameContaining(namePart);

        boolean contained = productsDB.get(0).getName().contains(namePart);
        assertTrue(contained);
    }
}
