package comidev.comistore.components.invoice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.invoice.dto.InvoiceReq;
import comidev.comistore.components.invoice_item.InvoiceItem;
import comidev.comistore.components.invoice_item.dto.InvoiceItemReq;
import comidev.comistore.components.product.Product;
import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.services.AppFabric;
import comidev.comistore.services.Json;

@ApiIntegrationTest
public class InvoiceControllerTest {
    @Autowired
    private AppFabric fabric;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Json json;

    @BeforeEach
    void setUp() {
        fabric.getInvoiceRepo().deleteAll();
        fabric.getInvoiceItemRepo().deleteAll();
    }

    // * GET, /invoices
    @Test
    void NO_CONTENT_CuandoNoHayCompras_findAll() throws Exception {
        String authorization = fabric.createToken("ADMIN");
        ResultActions res = mockMvc.perform(get("/invoices").header("Authorization", authorization));

        res.andExpect(status().isNoContent());
    }

    @Test
    void OK_CuandoHayAlMenosUnaCompra_findAll() throws Exception {
        String authorization = fabric.createToken("ADMIN");
        fabric.createInvoice(null, null);

        ResultActions res = mockMvc.perform(get("/invoices").header("Authorization", authorization));

        res.andExpect(status().isOk());
    }

    // * GET, /invoices/customer/{id}
    @Test
    void NOT_FUND_CuandoElClienteNoExiste_findByCustomerId() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        ResultActions res = mockMvc.perform(get("/invoices/customer/123").header("Authorization", authorization));

        res.andExpect(status().isNotFound());
    }

    @Test
    void NO_CONTENT_CuandoElClienteNoTieneCompras_findByCustomerId() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        Customer customerDB = fabric.createCustomer(null, null, null);

        ResultActions res = mockMvc
                .perform(get("/invoices/customer/" + customerDB.getId()).header("Authorization", authorization));

        res.andExpect(status().isNoContent());
    }

    @Test
    void OK_CuandoElClienteTieneAlMenosUnaCompra_findByCustomerId() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        Customer customerDB = fabric.createCustomer(null, null, null);
        InvoiceItem invoiceItemDB = fabric.createInvoiceItem(null);
        fabric.createInvoice(customerDB, invoiceItemDB);

        ResultActions res = mockMvc
                .perform(get("/invoices/customer/" + customerDB.getId()).header("Authorization", authorization));

        res.andExpect(status().isOk());
    }

    // * POST, /invoices
    @Test
    void BAD_REQUEST_CuandoHayErrorDeValidacion_save() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        InvoiceReq body = new InvoiceReq("", -1l, List.of());

        ResultActions res = mockMvc.perform(post("/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization", authorization));

        res.andExpect(status().isBadRequest());
    }

    @Test
    void NOT_FOUND_CuandoElClienteNoExiste_save() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        InvoiceReq body = new InvoiceReq("x", 123l,
                List.of(new InvoiceItemReq(1, 1l)));

        ResultActions res = mockMvc.perform(post("/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization", authorization));

        res.andExpect(status().isNotFound());
    }

    @Test
    void NOT_FOUND_CuandoAlMenosUnProductoNoExiste_save() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        Customer customerDB = fabric.createCustomer(null, null, null);
        InvoiceReq body = new InvoiceReq("x", customerDB.getId(),
                List.of(new InvoiceItemReq(1, 123l)));

        ResultActions res = mockMvc.perform(post("/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization", authorization));

        res.andExpect(status().isNotFound());
    }

    @Test
    void CREATED_CuandoSeRegistraCorrectamenteLaCompra_save() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        Customer customerDB = fabric.createCustomer(null, null, null);
        Product productDB = fabric.createProduct(null);
        InvoiceReq body = new InvoiceReq("x", customerDB.getId(),
                List.of(new InvoiceItemReq(1, productDB.getId())));

        ResultActions res = mockMvc.perform(post("/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJson(body)).header("Authorization", authorization));

        res.andExpect(status().isCreated());
    }
}
