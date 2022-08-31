package comidev.comistore.components.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

import comidev.comistore.components.user.request.UserCreate;
import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.helpers.Fabric;
import comidev.comistore.helpers.Request;
import comidev.comistore.helpers.Response;

@ApiIntegrationTest
public class UserControllerTest {
    @Autowired
    private Fabric fabric;
    @Autowired
    private Request request;

    // * GET /users
    @Test
    void OK_cuando_tiene_al_menos_un_usuario() throws Exception {
        User userDB = fabric.createUser(null, null);

        Response response = request.get("/users")
                .send();

        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.bodyContains(userDB.getUsername()));
    }

    // * POST, /users
    @Test
    void BAD_REQUEST_cuando_hay_error_de_validacion() throws Exception {
        UserCreate body = new UserCreate("us", "12");

        Response response = request.post("/users")
                .body(body)
                .send();

        assertEquals(HttpStatus.BAD_REQUEST, response.status());
    }

    @Test
    void CONFLICT_cuando_el_username_ya_existe() throws Exception {
        String username = fabric.createUser(null, null).getUsername();
        UserCreate body = new UserCreate(username, "12346");

        Response response = request.post("/users")
                .body(body)
                .send();

        assertEquals(HttpStatus.CONFLICT, response.status());
    }

    @Test
    void CREATED_cuando_se_registra_correctamente() throws Exception {
        UserCreate body = new UserCreate(fabric.random(), "12345");

        Response response = request.post("/users")
                .body(body)
                .send();

        assertEquals(HttpStatus.CREATED, response.status());
    }

    // * POST, /users/username
    @Test // ? False, cuando no existe
    void OK_false_username() throws Exception {
        String username = fabric.random();

        Response response = request.get("/users/exists")
                .addParam("username", username)
                .send();

        assertEquals(HttpStatus.OK, response.status());
        assertFalse(response.body(Boolean.class));
    }

    @Test // ? True, cuando sí existe
    void OK_true_username() throws Exception {
        String username = "comidev";
        fabric.createUser(username, null);

        Response response = request.get("/users/exists")
                .addParam("username", username)
                .send();

        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.body(Boolean.class));
    }
}
