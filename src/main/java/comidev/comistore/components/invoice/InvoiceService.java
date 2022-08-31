package comidev.comistore.components.invoice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.customer.CustomerService;
import comidev.comistore.components.invoice.request.InvoiceCreate;
import comidev.comistore.components.invoice.response.InvoiceDetails;
import comidev.comistore.components.invoice_item.InvoiceItem;
import comidev.comistore.components.invoice_item.InvoiceItemService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InvoiceService {
    private final InvoiceRepo invoiceRepo;
    private final CustomerService customerService;
    private final InvoiceItemService invoiceItemService;

    public List<InvoiceDetails> getAllInvoices() {
        return invoiceRepo.findAll().stream()
                .map(InvoiceDetails::new)
                .collect(Collectors.toList());
    }

    public List<InvoiceDetails> getInvoiceByCustomer(Long id) {
        Customer customer = customerService.findCustomerById(id);

        return invoiceRepo.findByCustomer(customer).stream()
                .map(InvoiceDetails::new)
                .collect(Collectors.toList());
    }

    public void registerInvoice(InvoiceCreate body) {
        Customer customer = customerService.findCustomerById(
                body.getCustomerId());

        List<InvoiceItem> items = new ArrayList<>();
        float total = (float) body.getItems().stream()
                .mapToDouble(item -> {
                    InvoiceItem itemDB = invoiceItemService.registerInvoiceItem(item);
                    items.add(itemDB);
                    return item.getQuantity() * itemDB.getProduct().getPrice();
                })
                .sum();

        Invoice invoiceNew = new Invoice(body);
        invoiceNew.setCustomer(customer);
        invoiceNew.setInvoiceItems(items);
        invoiceNew.setTotal(total);
        invoiceRepo.save(invoiceNew);
    }
}
