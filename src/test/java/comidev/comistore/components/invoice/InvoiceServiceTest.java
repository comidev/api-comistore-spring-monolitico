package comidev.comistore.components.invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.customer.CustomerRepo;
import comidev.comistore.components.invoice.dto.InvoiceReq;
import comidev.comistore.components.invoice.dto.InvoiceRes;
import comidev.comistore.components.invoice_item.InvoiceItem;
import comidev.comistore.components.invoice_item.InvoiceItemService;
import comidev.comistore.components.invoice_item.dto.InvoiceItemReq;
import comidev.comistore.components.product.Product;
import comidev.comistore.exceptions.HttpException;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {
    private InvoiceService invoiceService;
    @Mock
    private InvoiceRepo invoiceRepo;
    @Mock
    private CustomerRepo customerRepo;
    @Mock
    private InvoiceItemService invoiceItemService;

    @BeforeEach
    void setUp() {
        this.invoiceService = new InvoiceService(invoiceRepo, customerRepo,
                invoiceItemService);
    }

    @Test
    void testFindAll_PuedeDevolverLasCompras() {
        // Arreglar
        Product product = new Product();
        InvoiceItem invoiceItem = new InvoiceItem(1, product);
        Invoice invoice = new Invoice();
        invoice.setInvoiceItems(List.of(invoiceItem));

        when(invoiceRepo.findAll()).thenReturn(List.of(invoice));

        // Actuar
        List<InvoiceRes> invoicesRes = invoiceService.findAll();

        // Afirmar
        assertEquals(invoicesRes.get(0).getDescription(), invoice.getDescription());
        verify(invoiceRepo).findAll();
    }

    @Test
    void testFindByCustomerId_PuedeDevolverLasComprasDeUnCliente() {
        // Arreglar
        Long id = 1l;
        Customer customer = new Customer();

        Product product = new Product();
        InvoiceItem invoiceItem = new InvoiceItem(1, product);
        Invoice invoice = new Invoice();
        invoice.setInvoiceItems(List.of(invoiceItem));

        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));
        when(invoiceRepo.findByCustomer(customer)).thenReturn(List.of(invoice));

        // Actuar
        List<InvoiceRes> invoicesRes = invoiceService.findByCustomerId(id);

        // Afirmar
        assertEquals(invoicesRes.get(0).getDescription(), invoice.getDescription());
        verify(customerRepo).findById(id);
        verify(invoiceRepo).findByCustomer(customer);
    }

    @Test
    void testFindByCustomerId_PuedeArrojarNotFoundSiElClienteNoExiste() {
        // Arreglar
        Long id = 1l;

        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            invoiceService.findByCustomerId(id);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(customerRepo).findById(id);
    }

    @Test
    void testSave_PuedeArrojarNotFoundSiElClienteNoExiste() {
        // Arreglar
        Long id = 1l;
        InvoiceReq invoiceReq = new InvoiceReq();
        invoiceReq.setCustomerId(id);

        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            invoiceService.save(invoiceReq);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(customerRepo).findById(id);
    }

    @Test
    void testSave_PuedeGuardarUnaCompraDeUnCliente() {
        // Arreglar
        Long id = 1l;
        Integer quantity = 1;

        Customer customer = new Customer();

        Product product = new Product();
        product.setId(id);
        product.setStock(1);
        product.setPrice(1f);

        InvoiceItemReq invoiceItemReq = new InvoiceItemReq(quantity, id);

        InvoiceReq invoiceReq = new InvoiceReq();
        invoiceReq.setCustomerId(id);
        invoiceReq.setItems(List.of(invoiceItemReq));

        InvoiceItem invoiceItem = new InvoiceItem(quantity, product);

        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));
        when(invoiceItemService.saveInvoiceItem(invoiceItemReq))
                .thenReturn(invoiceItem);

        // Actuar
        invoiceService.save(invoiceReq);

        // Afirmar
        verify(customerRepo).findById(id);
        verify(invoiceItemService).saveInvoiceItem(invoiceItemReq);
        ArgumentCaptor<Invoice> invoiceAC = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepo).save(invoiceAC.capture());
    }
}
