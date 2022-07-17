package comidev.comistore.components.invoice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.customer.CustomerRepo;
import comidev.comistore.components.invoice.dto.InvoiceReq;
import comidev.comistore.components.invoice.dto.InvoiceRes;
import comidev.comistore.components.invoice_item.InvoiceItem;
import comidev.comistore.components.invoice_item.InvoiceItemRepo;
import comidev.comistore.components.invoice_item.dto.InvoiceItemReq;
import comidev.comistore.components.product.Product;
import comidev.comistore.components.product.ProductRepo;
import comidev.comistore.exceptions.HttpException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InvoiceService {
    private final InvoiceRepo invoiceRepo;
    private final InvoiceItemRepo invoiceItemRepo;
    private final CustomerRepo customerRepo;
    private final ProductRepo productRepo;

    public List<InvoiceRes> findAll() {
        return invoiceRepo.findAll().stream()
                .map(InvoiceRes::new).toList();
    }

    public List<InvoiceRes> findByCustomerId(Long id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> {
                    String message = "El cliente no existe!";
                    return new HttpException(HttpStatus.NOT_FOUND, message);
                });

        List<Invoice> invoicesDB = invoiceRepo.findByCustomer(customer);

        return invoicesDB.stream()
                .map(InvoiceRes::new)
                .toList();
    }

    public void save(InvoiceReq invoiceReq) {
        Invoice invoiceNew = new Invoice(invoiceReq);

        Customer customer = customerRepo.findById(invoiceReq.getCustomerId())
                .orElseThrow(() -> {
                    String message = "El cliente no existe!!";
                    return new HttpException(HttpStatus.NOT_FOUND, message);
                });

        Float total = 0f;
        List<InvoiceItem> items = new ArrayList<>();
        for (InvoiceItemReq item : invoiceReq.getItems()) {

            InvoiceItem invoiceItemNew = new InvoiceItem(item);

            Product productDB = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> {
                        String message = "El producto no existe!!";
                        return new HttpException(HttpStatus.NOT_FOUND, message);
                    });

            Integer stock = productDB.getStock();
            Integer quantity = item.getQuantity();
            productDB.setStock(stock - quantity);

            total += quantity * productDB.getPrice();

            invoiceItemNew.setProduct(productRepo.save(productDB));

            items.add(invoiceItemRepo.save(invoiceItemNew));
        }

        invoiceNew.setCustomer(customer);
        invoiceNew.setInvoiceItems(items);
        invoiceNew.setTotal(total);

        invoiceRepo.save(invoiceNew);
    }
}
