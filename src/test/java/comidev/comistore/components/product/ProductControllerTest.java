package comidev.comistore.components.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import comidev.comistore.components.category.Category;
import comidev.comistore.components.product.dto.ProductReq;
import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.services.AppFabric;
import comidev.comistore.services.Json;

@ApiIntegrationTest
public class ProductControllerTest {
    @Autowired
    private AppFabric fabric;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Json json;


    // * GET, /products
    @Test
    void OK_CuandoHayAlMenosUnProducto_findAllOrFields() throws Exception {
        fabric.createProduct(null);

        ResultActions res = mockMvc.perform(get("/products"));

        res.andExpect(status().isOk());
    }

    @Test
    void OK_CuandoHayAlMenosUnProductoQueContieneElNombre_findAllOrFields() throws Exception {
        String name = "Teclado Portatil";
        fabric.createProduct("Contengo " + name + " este nombre");

        ResultActions res = mockMvc.perform(get("/products?name=" + name));

        res.andExpect(status().isOk());
    }

    @Test
    void NOT_FOUND_CuandoLaCategoriaNoExiste_findAllOrFields() throws Exception {
        String category = "NO EXISTO";

        ResultActions res = mockMvc.perform(get("/products?category=" + category));

        res.andExpect(status().isNotFound());
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

        ResultActions res = mockMvc.perform(get("/products?category=" + category));

        res.andExpect(status().isOk());
    }

    // * GET, /products/{id}
    @Test
    void NOT_FOUND_CuandoNoExisteElProducto_findById() throws Exception {
        ResultActions res = mockMvc.perform(get("/products/123"));

        res.andExpect(status().isNotFound());
    }

    @Test
    void OK_CuandoExisteElProducto_findById() throws Exception {
        Product productDB = fabric.createProduct(null);

        ResultActions res = mockMvc.perform(get("/products/" + productDB.getId()));

        res.andExpect(status().isOk());
    }

    // * POST, /products
    @Test
    void BAD_REQUEST_CuandoHayErrorDeValidacion_save() throws Exception {
        String authorization = fabric.createToken("ADMIN");
        ProductReq productReq = new ProductReq("name", "photoUrl",
                "description", -100, 10.5f, List.of());

        ResultActions res = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(productReq)).header("Authorization", authorization));

        res.andExpect(status().isBadRequest());
    }

    @Test
    void NOT_FOUND_CuandoUnoOMasCategoriasNoExisten_save() throws Exception {
        String authorization = fabric.createToken("ADMIN");
        ProductReq productReq = new ProductReq("name", "photoUrl",
                "description", 100, 10.5f, List.of("NO existo"));

        ResultActions res = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(productReq)).header("Authorization", authorization));

        res.andExpect(status().isNotFound());
    }

    @Test
    void CREATED_CuandoTodoEsCorrecto_save() throws Exception {
        String authorization = fabric.createToken("ADMIN");
        String category = "Tecnologia";
        fabric.createCategory(category);
        ProductReq productReq = new ProductReq("name", "photoUrl",
                "description", 100, 10.5f, List.of(category));

        ResultActions res = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(productReq)).header("Authorization", authorization));

        res.andExpect(status().isCreated());
    }
}
