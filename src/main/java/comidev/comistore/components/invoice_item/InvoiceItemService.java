package comidev.comistore.components.invoice_item;

import org.springframework.stereotype.Service;

import comidev.comistore.components.invoice_item.request.InvoiceItemCreate;
import comidev.comistore.components.product.Product;
import comidev.comistore.components.product.ProductService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InvoiceItemService {
    private final InvoiceItemRepo invoiceItemRepo;
    private final ProductService productService;

    public InvoiceItem registerInvoiceItem(InvoiceItemCreate body) {
        Product productDB = productService.updateStock(
                body.getProductId(),
                (body.getQuantity()) * (-1));

        InvoiceItem invoiceItemNew = new InvoiceItem(body, productDB);

        return invoiceItemRepo.save(invoiceItemNew);
    }
}
