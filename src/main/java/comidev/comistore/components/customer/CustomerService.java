package comidev.comistore.components.customer;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import comidev.comistore.components.country.Country;
import comidev.comistore.components.country.CountryRepo;
import comidev.comistore.components.customer.dto.CustomerReq;
import comidev.comistore.components.customer.dto.CustomerRes;
import comidev.comistore.components.customer.dto.CustomerUpdate;
import comidev.comistore.components.user.User;
import comidev.comistore.components.user.UserService;
import comidev.comistore.exceptions.HttpException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepo customerRepo;
    private final UserService userService;
    private final CountryRepo countryRepo;

    public List<CustomerRes> findAll() {
        return customerRepo.findAll().stream()
                .map(CustomerRes::new)
                .toList();
    }

    private Customer getById(Long id) {
        return customerRepo.findById(id).orElseThrow(() -> {
            String message = "El cliente no existe!";
            return new HttpException(HttpStatus.NOT_FOUND, message);
        });
    }

    public CustomerRes findById(Long id) {
        return new CustomerRes(getById(id));
    }

    private Country findCountryByName(String name) {
        return countryRepo.findByName(name).orElseThrow(() -> {
            String message = "El pais no existe!";
            return new HttpException(HttpStatus.NOT_FOUND, message);
        });
    }

    public CustomerRes save(CustomerReq customerReq) {
        boolean existsEmail = customerRepo.existsByEmail(customerReq.getEmail());
        if (existsEmail) {
            String message = "El email ya existe";
            throw new HttpException(HttpStatus.CONFLICT, message);
        }

        Customer customerNew = new Customer(customerReq);

        User userDB = userService.saveCliente(customerReq.getUser());
        customerNew.setUser(userDB);

        Country countryDB = findCountryByName(customerReq.getCountry());
        customerNew.setCountry(countryDB);

        return new CustomerRes(customerRepo.save(customerNew));
    }

    public CustomerRes update(CustomerUpdate customerUpdate, Long id) {
        Customer customerDB = getById(id);

        String emailNew = customerUpdate.getEmail();
        if (!customerDB.getEmail().equals(emailNew)) {
            boolean existsEmail = customerRepo.existsByEmail(emailNew);
            if (existsEmail) {
                String message = "El email ya existe";
                throw new HttpException(HttpStatus.CONFLICT, message);
            }
            customerDB.setEmail(emailNew);
        }

        String usernamePrev = customerDB.getUser().getUsername();
        String usernameNew = customerUpdate.getUsername();
        if (!usernamePrev.equals(usernameNew)) {
            userService.updateUsername(usernamePrev, usernameNew);
        }

        String countryNew = customerUpdate.getCountry();
        if (!customerDB.getCountry().getName().equals(countryNew)) {
            Country countryDB = findCountryByName(countryNew);
            customerDB.setCountry(countryDB);
        }

        customerDB.setName(customerUpdate.getName());
        customerDB.setGender(customerUpdate.getGender());
        customerDB.setDateOfBirth(customerUpdate.getDateOfBirth());
        customerDB.setPhotoUrl(customerUpdate.getPhotoUrl());

        return new CustomerRes(customerRepo.save(customerDB));
    }

    public void deleteById(Long id) {
        if (!customerRepo.existsById(id)) {
            String message = "El usuario no existe!!";
            throw new HttpException(HttpStatus.NOT_FOUND, message);
        }
        customerRepo.deleteById(id);
    }

    public void existsEmail(String email) {
        if (!customerRepo.existsByEmail(email)) {
            String message = "El email no existe!";
            throw new HttpException(HttpStatus.NOT_FOUND, message);
        }
    }
}
