package comidev.comistore.components.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import comidev.comistore.components.user.User;
import comidev.comistore.components.user.request.UserCreate;
import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.helpers.Fabric;
import comidev.comistore.helpers.Request;
import comidev.comistore.helpers.Response;
import comidev.comistore.services.jwt.Tokens;

@ApiIntegrationTest
public class AuthControllerTest {
    @Autowired
    private Fabric fabric;
    @Autowired
    private Request request;

    // * POST, /auths/login
    @Test
    void BAD_REQUEST_CuandoHayErrorDeValidacion_login() throws Exception {
        UserCreate body = new UserCreate("co", "12");

        Response response = request.post("/auths/login")
                .body(body)
                .send();

        assertEquals(HttpStatus.BAD_REQUEST, response.status());
    }

    @Test
    void UNAUTHORIZED_CuandoElUsernameNoExiste_login() throws Exception {
        UserCreate body = new UserCreate(fabric.random(), "123");

        Response response = request.post("/auths/login")
                .body(body)
                .send();

        assertEquals(HttpStatus.UNAUTHORIZED, response.status());
    }

    @Test
    void UNAUTHORIZED_CuandoELPasswordEsIncorrecto_login() throws Exception {
        String username = fabric.createUser(null, null).getUsername();
        UserCreate body = new UserCreate(username, "1235");

        Response response = request.post("/auths/login")
                .body(body)
                .send();

        assertEquals(HttpStatus.UNAUTHORIZED, response.status());
    }

    @Test
    void OK_CuandoTodoEsCorrecto_login() throws Exception {
        String password = "omarxd";
        User user = fabric.createUser(null, password);
        String username = user.getUsername();
        UserCreate body = new UserCreate(username, password);

        Response response = request.post("/auths/login")
                .body(body)
                .send();

        assertEquals(HttpStatus.OK, response.status());
        Tokens tokens = response.body(Tokens.class);
        assertNotNull(tokens);
        assertNotNull(tokens.getAccess_token());
        assertTrue(fabric.getJwtService().isBearer("Bearer " + tokens.getAccess_token()));
    }

    // * POST, /auths/token/refresh
    @Test // ? cuando el token es incorrecto
    void UNAUTHORIZED_CuandoElTokenEsIncorrecto_token_refresh() throws Exception {
        String Authorization = "Bearer xddd.xddd.xdddd";

        Response response = request.post("/auths/token/refresh")
                .authorization(Authorization)
                .send();

        assertEquals(HttpStatus.UNAUTHORIZED, response.status());
    }

    @Test // ? cuando el token es correcto y devuelve los tokens
    void OK_CuandoElTokenEsCorrecto_token_refresh() throws Exception {
        String Authorization = fabric.createToken();

        Response response = request.post("/auths/token/refresh")
                .authorization(Authorization)
                .send();

        assertEquals(HttpStatus.OK, response.status());
        Tokens tokens = response.body(Tokens.class);
        assertNotNull(tokens);
        assertNotNull(tokens.getAccess_token());
        assertTrue(fabric.getJwtService().isBearer("Bearer " + tokens.getAccess_token()));
    }

    // * POST, /auths/token/validate
    @Test // ? false, cuando el token no es correcto
    void UNAUTHORIZED_CuandoElTokenNoEsValido_token_validate() throws Exception {
        String Authorization = "Bearer xdxdd.xdxxdxd.xdxdxddd";

        Response response = request.post("/auths/token/validate")
                .authorization(Authorization)
                .send();

        assertEquals(HttpStatus.UNAUTHORIZED, response.status());
    }

    @Test // ? true, cuando el token es correcto
    void OK_CuandoElTokenEsValido_token_validate() throws Exception {
        String Authorization = fabric.createToken();

        Response response = request.post("/auths/token/validate")
                .authorization(Authorization)
                .send();

        assertEquals(HttpStatus.OK, response.status());
        boolean isValid = response.body(Boolean.class);
        assertTrue(isValid);
    }
}
