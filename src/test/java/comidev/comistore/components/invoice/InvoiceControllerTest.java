package comidev.comistore.components.invoice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.invoice.request.InvoiceCreate;
import comidev.comistore.components.invoice_item.InvoiceItem;
import comidev.comistore.components.invoice_item.request.InvoiceItemCreate;
import comidev.comistore.components.product.Product;
import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.helpers.Fabric;
import comidev.comistore.helpers.Request;
import comidev.comistore.helpers.Response;

@ApiIntegrationTest
public class InvoiceControllerTest {
    @Autowired
    private Fabric fabric;
    @Autowired
    private Request request;

    // * GET, /invoices
    @Test
    void OK_CuandoHayAlMenosUnaCompra_findAll() throws Exception {
        String authorization = fabric.createToken("ADMIN");
        fabric.createInvoice(null, null);

        Response res = request.get("/invoices").authorization(authorization).send();

        assertEquals(HttpStatus.OK, res.status());
    }

    // * GET, /invoices/customer/{id}
    @Test
    void NOT_FUND_CuandoElClienteNoExiste_findByCustomerId() throws Exception {
        String authorization = fabric.createToken("CLIENTE");

        Response res = request.get("/invoices/customer/123").authorization(authorization).send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }


    @Test
    void OK_CuandoElClienteTieneAlMenosUnaCompra_findByCustomerId() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        Customer customerDB = fabric.createCustomer(null, null, null);
        InvoiceItem invoiceItemDB = fabric.createInvoiceItem(null);
        fabric.createInvoice(customerDB, invoiceItemDB);

        Response res = request.get("/invoices/customer/" + customerDB.getId())
                .authorization(authorization).send();

        assertEquals(HttpStatus.OK, res.status());
    }

    // * POST, /invoices
    @Test
    void BAD_REQUEST_CuandoHayErrorDeValidacion_save() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        InvoiceCreate body = new InvoiceCreate("", -1l, List.of());

        Response res = request.post("/invoices")
                .authorization(authorization)
                .body(body)
                .send();

        assertEquals(HttpStatus.BAD_REQUEST, res.status());
    }

    @Test
    void NOT_FOUND_CuandoElClienteNoExiste_save() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        InvoiceCreate body = new InvoiceCreate("x", 123l,
                List.of(new InvoiceItemCreate(1, 1l)));

        Response res = request.post("/invoices")
                .authorization(authorization)
                .body(body)
                .send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void NOT_FOUND_CuandoAlMenosUnProductoNoExiste_save() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        Customer customerDB = fabric.createCustomer(null, null, null);
        InvoiceCreate body = new InvoiceCreate("x", customerDB.getId(),
                List.of(new InvoiceItemCreate(1, 123l)));

        Response res = request.post("/invoices")
                .authorization(authorization)
                .body(body)
                .send();

        assertEquals(HttpStatus.NOT_FOUND, res.status());
    }

    @Test
    void CREATED_CuandoSeRegistraCorrectamenteLaCompra_save() throws Exception {
        String authorization = fabric.createToken("CLIENTE");
        Customer customerDB = fabric.createCustomer(null, null, null);
        Product productDB = fabric.createProduct(null);
        InvoiceCreate body = new InvoiceCreate("x", customerDB.getId(),
                List.of(new InvoiceItemCreate(1, productDB.getId())));

        Response res = request.post("/invoices")
                .authorization(authorization)
                .body(body)
                .send();

        assertEquals(HttpStatus.CREATED, res.status());
    }
}
