package comidev.comistore.components.customer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comidev.comistore.components.country.Country;
import comidev.comistore.components.country.CountryService;
import comidev.comistore.components.customer.request.CustomerCreate;
import comidev.comistore.components.customer.request.CustomerUpdate;
import comidev.comistore.components.customer.response.CustomerDetails;
import comidev.comistore.components.user.User;
import comidev.comistore.components.user.UserService;
import comidev.comistore.exceptions.HttpException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepo customerRepo;
    private final UserService userService;
    private final CountryService countryService;

    public List<CustomerDetails> getAllCustomers() {
        return customerRepo.findAll().stream()
                .map(CustomerDetails::new)
                .collect(Collectors.toList());
    }

    public CustomerDetails getCustomerById(Long id) {
        return new CustomerDetails(findCustomerById(id));
    }

    public Customer findCustomerById(Long id) {
        return customerRepo.findById(id).orElseThrow(() -> {
            String message = "El cliente no existe!";
            return new HttpException(HttpStatus.NOT_FOUND, message);
        });
    }

    @Transactional
    public CustomerDetails registerCustomer(CustomerCreate body) {
        if (existsEmail(body.getEmail())) {
            String message = "El email ya existe";
            throw new HttpException(HttpStatus.CONFLICT, message);
        }

        User user = userService.registerCustomer(body.getUser());
        Country country = countryService.findCountryByName(body.getCountry());

        Customer customer = new Customer(body, user, country);
        return new CustomerDetails(customerRepo.save(customer));
    }

    @Transactional
    public CustomerDetails updateCustomer(Long id, CustomerUpdate body) {
        Customer customerDB = findCustomerById(id);

        String email = body.getEmail();
        if (!customerDB.getEmail().equals(email) && existsEmail(email)) {
            String message = "El email ya existe";
            throw new HttpException(HttpStatus.CONFLICT, message);
        }

        String countryName = body.getCountry();
        boolean isSameCountry = customerDB.getCountry()
                .getName()
                .equals(countryName);
        Country country = isSameCountry ? customerDB.getCountry()
                : countryService.findCountryByName(countryName);

        userService.updateUser(customerDB.getUser(), body.getUser());
        customerDB.update(body, country);
        return new CustomerDetails(customerRepo.save(customerDB));
    }

    @Transactional
    public void deleteCustomer(Long id, String password) {
        Customer customer = findCustomerById(id);
        customerRepo.delete(customer);
        userService.deleteUser(customer.getUser(), password);
    }

    public boolean existsEmail(String email) {
        return customerRepo.existsByEmail(email);
    }
}
