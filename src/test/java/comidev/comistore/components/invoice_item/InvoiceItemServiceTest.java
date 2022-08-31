package comidev.comistore.components.invoice_item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import comidev.comistore.components.invoice_item.request.InvoiceItemCreate;
import comidev.comistore.components.product.Product;
import comidev.comistore.components.product.ProductService;

@ExtendWith(MockitoExtension.class)
public class InvoiceItemServiceTest {
    private InvoiceItemService invoiceItemService;
    @Mock
    private InvoiceItemRepo invoiceItemRepo;
    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        this.invoiceItemService = new InvoiceItemService(invoiceItemRepo, productService);
    }

    @Test
    void testSaveInvoiceItem_PuedeGuardarUnItemDeCompra() {
        // ! Arreglar
        InvoiceItemCreate body = new InvoiceItemCreate(1, 1l);

        Product productDB = new Product();
        InvoiceItem invoiceItem = new InvoiceItem(body, productDB);
        invoiceItem.setProduct(productDB);

        when(productService.updateStock(
                body.getProductId(),
                (body.getQuantity()) * (-1))).thenReturn(productDB);
        when(invoiceItemRepo.save(any())).thenReturn(invoiceItem);

        // ! Actuar
        InvoiceItem actual = invoiceItemService.registerInvoiceItem(body);

        // ! Afirmar
        assertEquals(body.getQuantity(), actual.getQuantity());

        verify(productService).updateStock(
                body.getProductId(),
                (body.getQuantity()) * (-1));

        ArgumentCaptor<InvoiceItem> invoiceItemAC = ArgumentCaptor
                .forClass(InvoiceItem.class);
        verify(invoiceItemRepo).save(invoiceItemAC.capture());
    }
}
