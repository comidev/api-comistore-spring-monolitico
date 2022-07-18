package comidev.comistore.components.invoice_item;

import org.springframework.stereotype.Service;

import comidev.comistore.components.invoice_item.dto.InvoiceItemReq;
import comidev.comistore.components.product.Product;
import comidev.comistore.components.product.ProductService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InvoiceItemService {
    private final InvoiceItemRepo invoiceItemRepo;
    private final ProductService productService;

    public InvoiceItem saveInvoiceItem(InvoiceItemReq item) {
        InvoiceItem invoiceItemNew = new InvoiceItem(item);

        Product productDB = productService.updateStock(
                item.getProductId(),
                (item.getQuantity()) * (-1));

        invoiceItemNew.setProduct(productDB);

        return invoiceItemRepo.save(invoiceItemNew);
    }
}
