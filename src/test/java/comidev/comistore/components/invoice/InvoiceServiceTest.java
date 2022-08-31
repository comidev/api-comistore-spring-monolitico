package comidev.comistore.components.invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.customer.CustomerService;
import comidev.comistore.components.invoice.request.InvoiceCreate;
import comidev.comistore.components.invoice.response.InvoiceDetails;
import comidev.comistore.components.invoice_item.InvoiceItem;
import comidev.comistore.components.invoice_item.InvoiceItemService;
import comidev.comistore.components.invoice_item.request.InvoiceItemCreate;
import comidev.comistore.components.product.Product;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {
    private InvoiceService invoiceService;
    @Mock
    private InvoiceRepo invoiceRepo;
    @Mock
    private CustomerService customerService;
    @Mock
    private InvoiceItemService invoiceItemService;

    @BeforeEach
    void setUp() {
        this.invoiceService = new InvoiceService(invoiceRepo,
                customerService, invoiceItemService);
    }

    // * getAllInvoices
    @Test
    void getAllInvoices_PuedeDevolverLasCompras() {
        // Arreglar
        Product product = new Product();
        InvoiceItemCreate invoiceDto = new InvoiceItemCreate(1, product.getId());
        InvoiceItem invoiceItem = new InvoiceItem(invoiceDto, product);
        Invoice invoice = new Invoice();
        invoice.setInvoiceItems(List.of(invoiceItem));

        when(invoiceRepo.findAll()).thenReturn(List.of(invoice));

        // Actuar
        List<InvoiceDetails> invoicesRes = invoiceService.getAllInvoices();

        // Afirmar
        assertEquals(invoicesRes.get(0).getDescription(), invoice.getDescription());
        verify(invoiceRepo).findAll();
    }

    // * getInvoiceByCustomer
    @Test
    void getInvoiceByCustomer_PuedeDevolverLasComprasDeUnCliente() {
        // Arreglar
        Long id = 1l;
        Customer customer = new Customer();
        Product product = new Product();
        InvoiceItemCreate invoiceDto = new InvoiceItemCreate(1, product.getId());
        InvoiceItem invoiceItem = new InvoiceItem(invoiceDto, product);
        Invoice invoice = new Invoice();
        invoice.setInvoiceItems(List.of(invoiceItem));

        when(customerService.findCustomerById(id)).thenReturn(customer);
        when(invoiceRepo.findByCustomer(customer)).thenReturn(List.of(invoice));

        // Actuar
        List<InvoiceDetails> invoicesRes = invoiceService.getInvoiceByCustomer(id);

        // Afirmar
        assertEquals(invoicesRes.get(0).getDescription(), invoice.getDescription());
        verify(customerService).findCustomerById(id);
        verify(invoiceRepo).findByCustomer(customer);
    }

    // * registerInvoice
    @Test
    void registerInvoice_PuedeGuardarUnaCompraDeUnCliente() {
        // Arreglar
        Long id = 1l;
        Integer quantity = 1;

        Customer customer = new Customer();

        Product product = new Product();
        product.setId(id);
        product.setStock(1);
        product.setPrice(1f);

        InvoiceItemCreate invoiceItemCreate = new InvoiceItemCreate(quantity, id);

        InvoiceCreate invoiceReq = new InvoiceCreate();
        invoiceReq.setCustomerId(id);
        invoiceReq.setItems(List.of(invoiceItemCreate));

        InvoiceItem invoiceItem = new InvoiceItem(invoiceItemCreate, product);

        when(customerService.findCustomerById(id)).thenReturn(customer);
        when(invoiceItemService.registerInvoiceItem(invoiceItemCreate))
                .thenReturn(invoiceItem);

        // Actuar
        invoiceService.registerInvoice(invoiceReq);

        // Afirmar
        verify(customerService).findCustomerById(id);
        verify(invoiceItemService).registerInvoiceItem(invoiceItemCreate);
        ArgumentCaptor<Invoice> invoiceAC = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepo).save(invoiceAC.capture());
    }
}
