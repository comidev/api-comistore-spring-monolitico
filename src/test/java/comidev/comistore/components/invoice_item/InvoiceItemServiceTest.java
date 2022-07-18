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

import comidev.comistore.components.invoice_item.dto.InvoiceItemReq;
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
        this.invoiceItemService = new InvoiceItemService(
                invoiceItemRepo,
                productService);
    }

    @Test
    void testSaveInvoiceItem_PuedeGuardarUnItemDeCompra() {
        // ! Arreglar
        InvoiceItemReq invoiceItemReq = new InvoiceItemReq(1, 1l);

        InvoiceItem invoiceItem = new InvoiceItem(invoiceItemReq);
        Product productDB = new Product();
        invoiceItem.setProduct(productDB);

        when(productService.updateStock(
                invoiceItemReq.getProductId(),
                (invoiceItemReq.getQuantity()) * (-1))).thenReturn(productDB);
        when(invoiceItemRepo.save(any())).thenReturn(invoiceItem);

        // ! Actuar
        InvoiceItem actual = invoiceItemService.saveInvoiceItem(invoiceItemReq);

        // ! Afirmar
        assertEquals(invoiceItemReq.getQuantity(), actual.getQuantity());

        verify(productService).updateStock(
                invoiceItemReq.getProductId(),
                (invoiceItemReq.getQuantity()) * (-1));

        ArgumentCaptor<InvoiceItem> invoiceItemAC = ArgumentCaptor
                .forClass(InvoiceItem.class);
        verify(invoiceItemRepo).save(invoiceItemAC.capture());
    }
}
