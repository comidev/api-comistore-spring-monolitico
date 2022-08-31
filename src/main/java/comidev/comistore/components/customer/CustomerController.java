package comidev.comistore.components.customer;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.customer.doc.CustomerDoc;
import comidev.comistore.components.customer.request.CustomerCreate;
import comidev.comistore.components.customer.request.CustomerUpdate;
import comidev.comistore.components.customer.response.CustomerDetails;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController implements CustomerDoc {
    private final CustomerService customerService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @ResponseBody
    public List<CustomerDetails> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/{id}")
    @ResponseBody
    public CustomerDetails getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CustomerDetails registerCustomer(
        @Valid @RequestBody CustomerCreate body) {
        return customerService.registerCustomer(body);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/{id}")
    @ResponseBody
    public CustomerDetails updateCustomer(@PathVariable Long id,
            @Valid @RequestBody CustomerUpdate body) {
        return customerService.updateCustomer(id, body);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id,
            @RequestParam(name = "password") String password) {
        customerService.deleteCustomer(id, password);
    }

    @PostMapping("/exists")
    public boolean existsEmail(@RequestParam(name = "email") String email) {
        return customerService.existsEmail(email);
    }
}
