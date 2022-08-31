package comidev.comistore.components.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import comidev.comistore.components.category.Category;
import comidev.comistore.components.product.request.ProductCreate;
import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.helpers.Fabric;
import comidev.comistore.helpers.Request;
import comidev.comistore.helpers.Response;

@ApiIntegrationTest
public class ProductControllerTest {
    @Autowired
    private Fabric fabric;
    @Autowired
    private Request request;

    // * GET, /products
    @Test
    void OK_CuandoHayAlMenosUnProducto_findAllOrFields() throws Exception {
        fabric.createProduct(null);

        Response res = request.get("/products").send();

        assertEquals(HttpStatus.OK, res.status());
    }

    @Test
    void OK_CuandoHayAlMenosUnProductoQueContieneElNombre_findAllOrFields() throws Exception {
        String name = "Teclado Portatil";
        fabric.createProduct("Contengo " + name + " este nombre");

        Response res = request.get("/products?name=" + name).send();

        assertEquals(HttpStatus.OK, res.status());
    }

    @Test
    void NOT_FOUND_CuandoLaCategoriaNoExiste_findAllOrFields() throws Exception {
        String category = "NO EXISTO";

        Response res = request.get("/products?category=" + category).send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void OK_CuandoLaCategoriaExisteYTieneAlMenosUnProducto_findAllOrFields() throws Exception {
        fabric.getCategoryRepo().deleteAll();
        String category = "Si existo";
        String name = "name";

        Category categoryDB1 = fabric.createCategory(category);
        Category categoryDB2 = fabric.createCategory("Tecnologia");

        Product product1 = fabric.createProduct(name);
        Product product2 = fabric.createProduct(name);
        Product product3 = fabric.createProduct(name);

        product1.getCategories().add(categoryDB1);

        product2.getCategories().add(categoryDB2);

        product3.getCategories().add(categoryDB1);
        product3.getCategories().add(categoryDB2);

        ProductRepo productRepo = fabric.getProductRepo();
        productRepo.save(product1);
        productRepo.save(product2);
        productRepo.save(product3);

        Response res = request.get("/products?category=" + category).send();

        assertEquals(HttpStatus.OK, res.status());
    }

    // * GET, /products/{id}
    @Test
    void NOT_FOUND_CuandoNoExisteElProducto_findById() throws Exception {
        Response res = request.get("/products/123").send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void OK_CuandoExisteElProducto_findById() throws Exception {
        Product productDB = fabric.createProduct(null);

        Response res = request.get("/products/" + productDB.getId()).send();

        assertEquals(HttpStatus.OK, res.status());
    }

    // * POST, /products
    @Test
    void BAD_REQUEST_CuandoHayErrorDeValidacion_save() throws Exception {
        String authorization = fabric.createToken("ADMIN");
        ProductCreate body = new ProductCreate("name", "photoUrl",
                "description", -100, 10.5f, List.of());

        Response res = request.post("/products")
                .authorization(authorization)
                .body(body)
                .send();

        assertEquals(HttpStatus.BAD_REQUEST, res.status());
    }

    @Test
    void NOT_FOUND_CuandoUnoOMasCategoriasNoExisten_save() throws Exception {
        String authorization = fabric.createToken("ADMIN");
        ProductCreate body = new ProductCreate("name", "photoUrl",
                "description", 100, 10.5f, List.of("NO existo"));

        Response res = request.post("/products")
                .authorization(authorization)
                .body(body)
                .send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void CREATED_CuandoTodoEsCorrecto_save() throws Exception {
        String authorization = fabric.createToken("ADMIN");
        String category = "Tecnologia";
        fabric.createCategory(category);
        ProductCreate body = new ProductCreate("name", "photoUrl",
                "description", 100, 10.5f, List.of(category));

        Response res = request.post("/products")
                .authorization(authorization)
                .body(body)
                .send();

        assertEquals(HttpStatus.CREATED, res.status());
    }
}
