package comidev.comistore.components.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;

import comidev.comistore.components.country.Country;
import comidev.comistore.components.customer.request.CustomerCreate;
import comidev.comistore.components.customer.request.CustomerUpdate;
import comidev.comistore.components.customer.util.Gender;
import comidev.comistore.components.user.User;
import comidev.comistore.components.user.request.UserCreate;
import comidev.comistore.components.user.request.UserUpdate;
import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.helpers.Fabric;
import comidev.comistore.helpers.Request;
import comidev.comistore.helpers.Response;

@ApiIntegrationTest
public class CustomerControllerTest {
    @Autowired
    private Fabric fabric;
    @Autowired
    private Request request;

    // * GET, /customers
    @Test
    void OK_CuandoHayAlMenosUnCliente_findAll() throws Exception {
        fabric.createCustomer(null, null, null);
        String authorization = fabric.createToken("ADMIN");

        Response res = request.get("/customers")
                .authorization(authorization).send();

        assertEquals(HttpStatus.OK, res.status());
    }

    // * GET, /customers/{id}
    @Test
    void NOT_FOUND_CuandoNoExisteElCliente_findById() throws Exception {
        String authorization = fabric.createToken("CLIENTE");

        Response res = request.get("/customers/123")
                .authorization(authorization).send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void OK_CuandoExisteElCliente_findById() throws Exception {
        Customer customerDB = fabric.createCustomer(null, null, null);
        String authorization = fabric.createToken("CLIENTE");

        Response res = request.get("/customers/" + customerDB.getId())
                .authorization(authorization).send();

        assertEquals(HttpStatus.OK, res.status());
    }

    // * POST, /customers
    @Test
    void BAD_REQUEST_CuandoHayErrorDeValidacion_save() throws Exception {
        UserCreate userReq = new UserCreate("co", "12");
        CustomerCreate body = new CustomerCreate("name", "email",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, "Perú");

        Response res = request.post("/customers")
                .body(body).send();

        assertEquals(HttpStatus.BAD_REQUEST, res.status());
    }

    @Test
    void CONFLICT_CuandoElEmailYaExiste_save() throws Exception {
        String email = "comidev.cdfgdsfgfdgontacto@gmail.com";
        fabric.createCustomer(email, null, null);

        UserCreate userReq = new UserCreate("comiddgdfgdfgev", "123");
        CustomerCreate body = new CustomerCreate("name", email,
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, "Perú");

        Response res = request.post("/customers")
                .body(body).send();

        assertEquals(HttpStatus.CONFLICT, res.status());
    }

    @Test
    void CONFLICT_CuandoElUsernameYaExiste_save() throws Exception {
        String username = "comidfgdfsdev";
        fabric.createUser(username, null);
        UserCreate userReq = new UserCreate(username, "123");
        CustomerCreate body = new CustomerCreate("name", "emaifdgfsdl@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, "Perú");

        Response res = request.post("/customers")
                .body(body).send();

        assertEquals(HttpStatus.CONFLICT, res.status());
    }

    @Test
    void NOT_FOUND_CuandoElPaisNoExiste_save() throws Exception {
        UserCreate userReq = new UserCreate("comgfgfgfdsgsidev", "1234");
        CustomerCreate body = new CustomerCreate("name", "emasdfgfdsgdfil@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, "Pefdgfdgdffdgrú");

        Response res = request.post("/customers")
                .body(body).send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void CREATED_CuandoSeGuardaCorrectamente_save() throws Exception {
        String country = "Perú";
        fabric.createCountry(country);
        UserCreate userReq = new UserCreate("comidsdfgfsdev", "123");
        CustomerCreate body = new CustomerCreate("name", "emaigdsfgsdl@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", userReq, country);

        Response res = request.post("/customers")
                .body(body).send();

        assertEquals(HttpStatus.CREATED, res.status());
    }

    // * PUT, /customers/{id}
    @Test
    void BAD_REQUEST_CuandoHayErrorDeValidacion_update() throws Exception {
        UserUpdate userUpdate = new UserUpdate("usegdsfgsdfgrname", "newPassword",
                "password");
        CustomerUpdate body = new CustomerUpdate("Omar", "omasdgdfsgrgmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)), "photoUrl", "Perú", userUpdate);
        String authorization = fabric.createToken("CLIENTE");

        Response res = request.put("/customers/123")
                .authorization(authorization)
                .body(body).send();

        assertEquals(HttpStatus.BAD_REQUEST, res.status());
    }

    @Test
    void NOT_FOUND_CuandoElClienteNoExiste_update() throws Exception {
        UserUpdate userUpdate = new UserUpdate("username", "newPassword",
                "password");
        CustomerUpdate body = new CustomerUpdate("Omar", "omar@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)),
                "photoUrl", "Perú", userUpdate);
        String authorization = fabric.createToken("CLIENTE");

        Response res = request.put("/customers/123")
                .authorization(authorization)
                .body(body).send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void CONFLICT_CuandoElEmailNuevoYaExiste_update() throws Exception {
        String email = "comidev.congdfgfdsgtacto@gmail.com";
        fabric.createCustomer(email, null, null);

        Customer customerDB = fabric.createCustomer(null, null, null);
        String authorization = fabric.createToken("CLIENTE");

        CustomerUpdate body = new CustomerUpdate("Omar", email,
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)),
                "photoUrl", "Bolivia",
                new UserUpdate("", "", ""));

        Response res = request.put("/customers/" + customerDB.getId())
                .authorization(authorization)
                .body(body).send();

        assertEquals(HttpStatus.CONFLICT, res.status());
    }

    @Test
    void CONFLICT_CuandoElUsernameNuevoYaExiste_update() throws Exception {
        String username = "comidfsgsdfgdsgsdfdev";
        String password = "password";
        String country = "xddf";
        Country countrydb = fabric.createCountry(country);
        fabric.createUser(username, password);
        User user = fabric.createUser(null, password);
        Customer customerDB = fabric.createCustomer(null, user, countrydb);
        String authorization = fabric.createToken("CLIENTE");

        UserUpdate userUpdate = new UserUpdate(username, "newPassword",
                password);
        CustomerUpdate body = new CustomerUpdate("Omar", "omadsfgsdfgr3@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)),
                "photoUrl", country, userUpdate);

        Response res = request.put("/customers/" + customerDB.getId())
                .authorization(authorization)
                .body(body).send();

        assertEquals(HttpStatus.CONFLICT, res.status());
    }

    @Test
    void NOT_FOUND_CuandoElPaisNuevoNoExiste_update() throws Exception {
        Customer customerDB = fabric.createCustomer(null, null, null);
        UserUpdate userUpdate = new UserUpdate("usegfdsgdfsgrname",
                "newPassword", "password");
        CustomerUpdate body = new CustomerUpdate("Omar", "omar3dfgsdfg@gmail.com",
                Gender.MALE, Date.valueOf(LocalDate.of(2000, 3, 11)),
                "photoUrl", "fsdfsfsd", userUpdate);
        String authorization = fabric.createToken("CLIENTE");

        Response res = request.put("/customers/" + customerDB.getId())
                .authorization(authorization)
                .body(body).send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void OK_CuandoSeGuardaCorrectamente_update() throws Exception {
        Country country = fabric.createCountry(null);
        String password = "xdfdfd";
        User user = fabric.createUser(null, password);
        Customer customer = fabric.createCustomer(null, user, country);

        UserUpdate userUpdate = new UserUpdate("username",
                "newPassword", password);
        CustomerUpdate body = new CustomerUpdate("Omar",
                "omar3@gmail.com", Gender.MALE,
                Date.valueOf(LocalDate.of(2000, 3, 11)),
                "photoUrl", country.getName(), userUpdate);
        String authorization = fabric.createToken("CLIENTE");

        Response res = request.put("/customers/" + customer.getId())
                .authorization(authorization)
                .body(body).send();

        assertEquals(HttpStatus.OK, res.status());
        assertTrue(res.bodyContains(body.getEmail()));
    }

    // * DELETE, /customers/{id}
    @Test
    void NOT_FOUND_CuandoElClienteNoExiste_deleteById() throws Exception {
        String authorization = fabric.createToken("CLIENTE");

        Response res = request.delete("/customers/123?password=" + "dsfsdff")
                .authorization(authorization).send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void OK_CuandoElClienteEsEliminado_deleteById() throws Exception {
        String password = "omarerej";
        User user = fabric.createUser(null, password);

        Customer customerDB = fabric.createCustomer(null, user, null);
        String authorization = fabric.createToken("CLIENTE");

        Response res = request.delete("/customers/" + customerDB.getId())
                .addParam("password", password)
                .authorization(authorization).send();

        assertEquals(HttpStatus.NO_CONTENT, res.status());
    }

    // * POST, /customers/email
    @Test
    void OK_CuandoElEmailExiste_existsEmail() throws Exception {
        String email = "comidev.contgdfgdfsgdfgacto@gmail.com";
        fabric.createCustomer(email, null, null);

        Response res = request.post("/customers/exists?email=" + email).send();

        assertEquals(HttpStatus.OK, res.status());
    }
}
