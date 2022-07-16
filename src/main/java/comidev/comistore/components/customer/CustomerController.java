package comidev.comistore.components.customer;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.customer.dto.CustomerReq;
import comidev.comistore.components.customer.dto.CustomerRes;
import comidev.comistore.components.customer.dto.CustomerUpdate;
import comidev.comistore.utils.EmailBody;
import comidev.comistore.utils.Validator;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerRes>> findAll() {

        List<CustomerRes> customers = customerService.findAll();

        return ResponseEntity
                .status(customers.isEmpty() ? 204 : 200)
                .body(customers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CustomerRes findById(@PathVariable Long id) {

        CustomerRes customer = customerService.findById(id);
        return customer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CustomerRes save(@Valid @RequestBody CustomerReq customerReq,
            BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        CustomerRes customer = customerService.save(customerReq);
        return customer;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CustomerRes update(@Valid @RequestBody CustomerUpdate customerReq, BindingResult bindingResult,
            @PathVariable Long id) {
        Validator.checkOrThrowBadRequest(bindingResult);

        CustomerRes customer = customerService.update(customerReq, id);
        return customer;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        customerService.deleteById(id);
    }

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public void existsEmail(@Valid @RequestBody EmailBody email, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        customerService.existsEmail(email.getEmail());
    }
}
