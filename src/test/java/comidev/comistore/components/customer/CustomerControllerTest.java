package comidev.comistore.components.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.time.LocalDate;

import comidev.comistore.components.customer.dto.CustomerReq;
import comidev.comistore.components.customer.dto.CustomerRes;
import comidev.comistore.components.customer.dto.CustomerUpdate;
import comidev.comistore.components.customer.dto.EmailBody;
import comidev.comistore.components.role.Role;
import comidev.comistore.components.role.RoleName;
import comidev.comistore.components.user.dto.UserReq;
import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.services.AppFabric;
import comidev.comistore.services.Json;

@ApiIntegrationTest
public class CustomerControllerTest {
    @Autowired
    private AppFabric fabric;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Json json;

    @BeforeEach
    void beforeEach() {
        fabric.getCustomerRepo().deleteAll();
        fabric.getUserRepo().deleteAll();
        fabric.getCountryRepo().deleteAll();
        fabric.getRoleRepo().deleteAll();
        fabric.getRoleRepo().save(new Role(RoleName.CLIENTE));
    }

    // * GET, /customers
    @Test
    void NO_CONTENT_CuandoNoHayClientes_findAll() throws Exception {
        String authorization = fabric.createToken("ADMIN");

        ResultActions res = mockMvc.perform(get("/customers")
                .header("Authorization", authorization));

        res.andExpect(status().isNoContent());
    }

    @Test
    void OK_CuandoHayAlMenosUnCliente_findAll() throws Exception {
        fabric.createCustomer(null, null, null);
        String authorization = fabric.createToken("ADMIN");

        ResultActions res = mockMvc.perform(get("/customers").header("Authorization", authorization));

        res.andExpect(status().isOk());
    }

    // * GET, /customers/{id}
    @Test
    void NOT_FOUND_CuandoNoExisteElCliente_findById() throws Exception {
        String authorization = fabric.createToken("CLIENTE");

        ResultActions res = mockMvc.perform(get("/customers/123").header("Authorization", authorization));

        res.andExpect(status().isNotFound());
    }

    @Test
    void OK_CuandoExisteElCliente_findById() throws Exception {
        Customer customerDB = fabric.createCustomer(null, null, null);
        String authorization = fabric.createToken("CLIENTE");

        ResultActions res = mockMvc.perform(get("/customers/" + customerDB.getId()).header("Authorization",
                authorization));

        res.andExpect(status().isOk());
    }

    // * POST, /customers
    @Test
    void BAD_REQUEST_CuandoHayErrorDeValidacion_save() throws Exception {
        UserReq userReq = new UserReq("co", "12");
        CustomerReq body = new CustomerReq("name", "email",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, "Perú");

        ResultActions res = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)));

        res.andExpect(status().isBadRequest());
    }

    @Test
    void CONFLICT_CuandoElEmailYaExiste_save() throws Exception {
        String email = "comidev.contacto@gmail.com";
        fabric.createCustomer(email, null, null);

        UserReq userReq = new UserReq("comidev", "123");
        CustomerReq body = new CustomerReq("name", email,
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, "Perú");

        ResultActions res = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)));

        res.andExpect(status().isConflict());
    }

    @Test
    void CONFLICT_CuandoElUsernameYaExiste_save() throws Exception {
        String username = "comidev";
        fabric.createUser(username, null);
        UserReq userReq = new UserReq(username, "123");
        CustomerReq body = new CustomerReq("name", "email@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, "Perú");

        ResultActions res = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)));

        res.andExpect(status().isConflict());
    }

    @Test
    void NOT_FOUND_CuandoElPaisNoExiste_save() throws Exception {
        UserReq userReq = new UserReq("comidev", "1234");
        CustomerReq body = new CustomerReq("name", "email@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, "Perú");

        ResultActions res = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)));

        res.andExpect(status().isNotFound());
    }

    @Test
    void CREATED_CuandoSeGuardaCorrectamente_save() throws Exception {
        String country = "Perú";
        fabric.createCountry(country);
        UserReq userReq = new UserReq("comidev", "123");
        CustomerReq body = new CustomerReq("name", "email@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, country);

        ResultActions res = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)));

        res.andExpect(status().isCreated());
    }

    // * PUT, /customers/{id}
    @Test
    void BAD_REQUEST_CuandoHayErrorDeValidacion_update() throws Exception {
        CustomerUpdate body = new CustomerUpdate("Omar", "omargmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", "com", "Perú");
        String authorization = fabric.createToken("CLIENTE");

        ResultActions res = mockMvc.perform(put("/customers/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization",
                        authorization));

        res.andExpect(status().isBadRequest());
    }

    @Test
    void NOT_FOUND_CuandoElClienteNoExiste_update() throws Exception {
        CustomerUpdate body = new CustomerUpdate("Omar", "omar@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", "comidev", "Perú");
        String authorization = fabric.createToken("CLIENTE");

        ResultActions res = mockMvc.perform(put("/customers/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization",
                        authorization));

        res.andExpect(status().isNotFound());
    }

    @Test
    void CONFLICT_CuandoElEmailNuevoYaExiste_update() throws Exception {
        String email = "comidev.contacto@gmail.com";
        fabric.createCustomer(email, null, null);
        Customer customerDB = fabric.createCustomer(null, null, null);
        String authorization = fabric.createToken("CLIENTE");

        CustomerUpdate body = new CustomerUpdate("Omar", email,
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)),
                "photoUrl", "username", "Bolivia");

        ResultActions res = mockMvc.perform(put("/customers/" + customerDB.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization",
                        authorization));

        res.andExpect(status().isConflict());
    }

    @Test
    void CONFLICT_CuandoElUsernameNuevoYaExiste_update() throws Exception {
        String username = "comidev";
        fabric.createUser(username, null);
        Customer customerDB = fabric.createCustomer(null, null, null);
        String authorization = fabric.createToken("CLIENTE");

        CustomerUpdate body = new CustomerUpdate("Omar", "omar3@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)),
                "photoUrl", username, "Bolivia");

        ResultActions res = mockMvc.perform(put("/customers/" + customerDB.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization",
                        authorization));

        res.andExpect(status().isConflict());
    }

    @Test
    void NOT_FOUND_CuandoElPaisNuevoNoExiste_update() throws Exception {
        Customer customerDB = fabric.createCustomer(null, null, null);

        CustomerUpdate body = new CustomerUpdate("Omar", "omar3@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)),
                "photoUrl", "username3", "Bolivia");
        String authorization = fabric.createToken("CLIENTE");

        ResultActions res = mockMvc.perform(put("/customers/" + customerDB.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization",
                        authorization));

        res.andExpect(status().isNotFound());
    }

    @Test
    void OK_CuandoSeGuardaCorrectamente_update() throws Exception {
        String country = "Colombia";
        fabric.createCountry(country);
        Customer customerDB = fabric.createCustomer(null, null, null);

        CustomerUpdate body = new CustomerUpdate("Omar", "omar3@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)),
                "photoUrl", "username3", country);
        String authorization = fabric.createToken("CLIENTE");

        ResultActions res = mockMvc.perform(put("/customers/" + customerDB.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization",
                        authorization));

        res.andExpect(status().isOk());
        String bodyRes = res.andReturn().getResponse().getContentAsString();
        CustomerRes customerRes = json.fromJson(bodyRes, CustomerRes.class);
        assertEquals(customerRes.getUsername(), body.getUsername());
    }

    // * DELETE, /customers/{id}
    @Test
    void NOT_FOUND_CuandoElClienteNoExiste_deleteById() throws Exception {
        String authorization = fabric.createToken("CLIENTE");

        ResultActions res = mockMvc.perform(delete("/customers/123").header("Authorization",
                authorization));

        res.andExpect(status().isNotFound());
    }

    @Test
    void OK_CuandoElClienteEsEliminado_deleteById() throws Exception {
        Customer customerDB = fabric.createCustomer(null, null, null);
        String authorization = fabric.createToken("CLIENTE");

        ResultActions res = mockMvc.perform(delete("/customers/" + customerDB.getId()).header("Authorization",
                authorization));

        res.andExpect(status().isOk());
    }

    // * POST, /customers/email
    @Test
    void NOT_FOUND_CuandoElEmailNoExiste_existsEmail() throws Exception {
        EmailBody body = new EmailBody("comidev@gmail.com");

        ResultActions res = mockMvc.perform(post("/customers/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)));

        res.andExpect(status().isNotFound());
    }

    @Test
    void OK_CuandoElEmailExiste_existsEmail() throws Exception {
        String email = "comidev.contacto@gmail.com";
        fabric.createCustomer(email, null, null);

        EmailBody body = new EmailBody(email);

        ResultActions res = mockMvc.perform(post("/customers/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)));

        res.andExpect(status().isOk());
    }
}
