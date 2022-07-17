package comidev.comistore.components.user;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import comidev.comistore.components.user.dto.Exists;
import comidev.comistore.components.user.dto.Passwords;
import comidev.comistore.components.user.dto.UserReq;
import comidev.comistore.components.user.dto.UserRes;
import comidev.comistore.components.user.dto.Username;
import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.services.AppFabric;
import comidev.comistore.services.Json;
import comidev.comistore.services.jwt.Tokens;
import comidev.comistore.utils.Updated;

@ApiIntegrationTest
public class UserControllerTest {
    @Autowired
    private AppFabric fabric;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Json json;

    @BeforeEach
    void beforeEach() {
        fabric.getUserRepo().deleteAll();
    }

    // * GET /users
    @Test
    void NO_CONTENT_cuando_esta_vacio() throws Exception {
        ResultActions response = mockMvc.perform(get("/users"));

        response.andExpect(status().isNoContent());
    }

    @Test
    void OK_cuando_tiene_al_menos_un_usuario() throws Exception {
        User userDB = fabric.createUser(null, null);

        ResultActions response = mockMvc.perform(get("/users"));

        response.andExpect(status().isOk());
        String body = response.andReturn().getResponse().getContentAsString();
        String expected = json.toJson(List.of(new UserRes(userDB)));
        assertEquals(expected, body);
    }

    // * POST, /users
    @Test
    void BAD_REQUEST_cuando_hay_error_de_validacion() throws Exception {
        UserReq bodyReq = new UserReq("us", "12");

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    void CONFLICT_cuando_el_username_ya_existe() throws Exception {
        String username = "comidev";
        fabric.createUser(username, null);
        UserReq bodyReq = new UserReq(username, "12346");

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isConflict());
    }

    @Test
    void CREATED_cuando_se_registra_correctamente() throws Exception {
        UserReq bodyReq = new UserReq("comidev", "12345");

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isCreated());
    }

    // * POST, /users/username
    @Test // ? cuando hay error de validación
    void BAD_REQUEST_username() throws Exception {
        Username bodyReq = new Username();

        ResultActions response = mockMvc.perform(post("/users/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isBadRequest());
    }

    @Test // ? False, cuando no existe
    void OK_false_username() throws Exception {
        Username bodyReq = new Username("comidev");

        ResultActions response = mockMvc.perform(post("/users/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isOk());
        String bodyRes = response.andReturn().getResponse().getContentAsString();
        String expected = json.toJson(new Exists(false));
        assertEquals(expected, bodyRes);
    }

    @Test // ? True, cuando sí existe
    void OK_true_username() throws Exception {
        String username = "comidev";
        fabric.createUser(username, null);
        Username bodyReq = new Username(username);

        ResultActions response = mockMvc.perform(post("/users/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isOk());
        String bodyRes = response.andReturn().getResponse().getContentAsString();
        String expected = json.toJson(new Exists(true));
        assertEquals(expected, bodyRes);
    }

    // * PATCH, /users/{id}/password
    @Test // ? cuando hay error de validación
    void BAD_REQUEST_CuandoHayErrorDeValidacion_password() throws Exception {
        Passwords bodyReq = new Passwords("1", "");
        String authorization = fabric.createToken("CLIENTE");

        ResultActions response = mockMvc.perform(patch("/users/" + "123"
                + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)).header("Authorization", authorization));

        response.andExpect(status().isBadRequest());
    }

    @Test // ? cuando no existe el usuario
    void NOT_FOUND_CuandoNoExisteElUsuario_password() throws Exception {
        Passwords bodyReq = new Passwords("nuevo", "viejo");
        String authorization = fabric.createToken("CLIENTE");

        ResultActions response = mockMvc.perform(patch("/users/" + "123"
                + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)).header("Authorization", authorization));

        response.andExpect(status().isNotFound());
    }

    @Test // ? cuando la contraseña no es correcta
    void UNAUTHORIZED_CuandoLaContrasenaNoEsCorrecta_password() throws Exception {
        User userDB = fabric.createUser(null, null);
        Passwords bodyReq = new Passwords("nuevo", "viejo");
        String authorization = fabric.createToken("CLIENTE");

        ResultActions response = mockMvc.perform(patch("/users/" + userDB.getId()
                + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)).header("Authorization", authorization));

        response.andExpect(status().isUnauthorized());
    }

    @Test // ? cuando se actualiza la contraseña
    void OK_CuandoSeActualizaCorrectamente_password() throws Exception {
        String password = "1234";
        User userDB = fabric.createUser(null, password);
        Passwords bodyReq = new Passwords("nuevo", password);
        String authorization = fabric.createToken("CLIENTE");

        ResultActions response = mockMvc.perform(patch("/users/" + userDB.getId()
                + "/password")
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isOk());
        String bodyRes = response.andReturn().getResponse().getContentAsString();
        String expected = json.toJson(new Updated(true));
        assertEquals(expected, bodyRes);
    }

    // * POST, /users/login
    @Test // ? Cuando hay error de validación
    void BAD_REQUEST_CuandoHayErrorDeValidacion_login() throws Exception {
        UserReq bodyReq = new UserReq("co", "12");

        ResultActions response = mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isBadRequest());
    }

    @Test // ? cuando el username no existe
    void UNAUTHORIZED_CuandoElUsernameNoExiste_login() throws Exception {
        UserReq bodyReq = new UserReq("comidev", "123");

        ResultActions response = mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isUnauthorized());
    }

    @Test // ? cuando el password es incorrecto
    void UNAUTHORIZED_CuandoELPasswordEsIncorrecto_login() throws Exception {
        String username = "comidev";
        fabric.createUser(username, null);
        UserReq bodyReq = new UserReq(username, "1235");

        ResultActions response = mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isUnauthorized());
    }

    @Test // ? cuando todo es correcto y devuelve el token
    void OK_CuandoTodoEsCorrecto_login() throws Exception {
        String username = "comidev";
        String password = "12345";
        fabric.createUser(username, password);
        UserReq bodyReq = new UserReq(username, password);

        ResultActions response = mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(bodyReq)));

        response.andExpect(status().isOk());
        String bodyRes = response.andReturn().getResponse().getContentAsString();
        Tokens tokens = json.fromJson(bodyRes, Tokens.class);
        assertNotNull(tokens);
        assertNotNull(tokens.getAccess_token());
        assertTrue(fabric.getJwtService().isBearer("Bearer " + tokens.getAccess_token()));
    }

    // * POST, /users/token/refresh
    @Test // ? cuando el token es incorrecto
    void UNAUTHORIZED_CuandoElTokenEsIncorrecto_token_refresh() throws Exception {
        String Authorization = "Bearer xddd.xddd.xdddd";

        ResultActions response = mockMvc.perform(post("/users/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", Authorization));

        response.andExpect(status().isUnauthorized());

    }

    @Test // ? cuando el token es correcto y devuelve los tokens
    void OK_CuandoElTokenEsCorrecto_token_refresh() throws Exception {
        String Authorization = fabric.createToken();

        ResultActions response = mockMvc.perform(post("/users/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", Authorization));

        response.andExpect(status().isOk());
        String bodyRes = response.andReturn().getResponse().getContentAsString();
        Tokens tokens = json.fromJson(bodyRes, Tokens.class);
        assertNotNull(tokens);
        assertNotNull(tokens.getAccess_token());
        assertTrue(fabric.getJwtService().isBearer("Bearer " + tokens.getAccess_token()));
    }

    // * POST, /users/token/validate
    @Test // ? false, cuando el token no es correcto
    void UNAUTHORIZED_CuandoElTokenNoEsValido_token_validate() throws Exception {
        String Authorization = "Bearer xdxdd.xdxxdxd.xdxdxddd";

        ResultActions response = mockMvc.perform(post("/users/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", Authorization));

        response.andExpect(status().isUnauthorized());
    }

    @Test // ? true, cuando el token es correcto
    void OK_CuandoElTokenEsValido_token_validate() throws Exception {
        String Authorization = fabric.createToken();

        ResultActions response = mockMvc.perform(post("/users/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", Authorization));

        response.andExpect(status().isOk());
        String bodyRes = response.andReturn().getResponse().getContentAsString();
        Tokens tokens = json.fromJson(bodyRes, Tokens.class);
        assertNotNull(tokens);
        assertNotNull(tokens.getAccess_token());
        assertTrue(fabric.getJwtService().isBearer("Bearer " + tokens.getAccess_token()));
    }
}
