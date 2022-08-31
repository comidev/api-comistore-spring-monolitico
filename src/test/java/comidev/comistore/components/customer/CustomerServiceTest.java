package comidev.comistore.components.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import comidev.comistore.components.country.Country;
import comidev.comistore.components.country.CountryService;
import comidev.comistore.components.country.request.CountryCreate;
import comidev.comistore.components.customer.request.CustomerCreate;
import comidev.comistore.components.customer.request.CustomerUpdate;
import comidev.comistore.components.customer.response.CustomerDetails;
import comidev.comistore.components.user.User;
import comidev.comistore.components.user.UserService;
import comidev.comistore.exceptions.HttpException;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    private CustomerService customerService;
    @Mock
    private CustomerRepo customerRepo;
    @Mock
    private UserService userService;
    @Mock
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        this.customerService = new CustomerService(customerRepo,
                userService, countryService);
    }

    // * getAllCustomers
    @Test
    void getAllCustomers_PuedeDevolverLosClientes() {
        // Arreglar
        User user = new User();
        Country country = new Country();
        Customer customer = new Customer(1l);
        customer.setUser(user);
        customer.setCountry(country);
        when(customerRepo.findAll()).thenReturn(List.of(customer));

        // Actuar
        List<CustomerDetails> customers = customerService.getAllCustomers();

        // Afirmar
        assertEquals(customers.get(0).getId(), new CustomerDetails(customer).getId());
        verify(customerRepo).findAll();
    }

    // * deleteCustomer
    @Test
    void testDeleteById_PuedeEliminarPorId() {
        // Arreglar
        Long id = 1l;
        Customer customer = new Customer(id);
        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));

        String password = "password";
        doNothing().when(userService).deleteUser(customer.getUser(), password);
        doNothing().when(customerRepo).delete(customer);

        // Actuar
        customerService.deleteCustomer(id, password);

        // Afirmar
        verify(customerRepo).findById(id);
        verify(userService).deleteUser(customer.getUser(), password);
        verify(customerRepo).delete(customer);
    }

    // * existsEmail
    @Test
    void testExistsEmail_PuedeVerificarSiExisteElEmail() {
        // Arreglar
        String email = "comidev.contacto@gmail.com";
        when(customerRepo.existsByEmail(email)).thenReturn(true);

        // Actuar
        boolean response = customerService.existsEmail(email);

        // Afirmar
        verify(customerRepo).existsByEmail(email);
        assertTrue(response);
    }

    // * getCustomerById
    @Test
    void getCustomerById_PuedeDevolverAlCliente() {
        // Arreglar
        Long id = 1l;
        User user = new User();
        Country country = new Country();
        Customer customer = new Customer(1l);
        customer.setUser(user);
        customer.setCountry(country);
        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));

        // Actuar
        CustomerDetails customerRes = customerService.getCustomerById(id);

        // Afirmar
        assertEquals(customerRes.getId(), new CustomerDetails(customer).getId());
        verify(customerRepo).findById(id);
    }

    @Test
    void testFindById_PuedeArrojarNotFoundSiNoExiste() {
        // Arreglar
        Long id = 1l;
        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.getCustomerById(id);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(customerRepo).findById(id);
    }

    // * registerCustomer
    @Test
    void registerCustomer_PuedeArrojarConflictSiElEmailExiste() {
        // Arreglar
        String email = "comidev.contacto@gmail.com";
        CustomerCreate customerReq = new CustomerCreate();
        customerReq.setEmail(email);
        when(customerRepo.existsByEmail(email)).thenReturn(true);

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.registerCustomer(customerReq);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.CONFLICT, status);
        verify(customerRepo).existsByEmail(email);
    }

    @Test
    void testSave_PuedeGuardarElCliente() {
        // Arreglar
        String email = "comidev.contacto@gmail.com";
        // String country = "Perú";
        // UserCreate user = new UserCreate("comidev", "123");

        CustomerCreate body = new CustomerCreate();
        body.setEmail(email);

        when(customerRepo.existsByEmail(email)).thenReturn(false);

        User userDB = new User();
        when(userService.registerCustomer(body.getUser())).thenReturn(userDB);

        Country countryDB = new Country();
        when(countryService.findCountryByName(body.getCountry())).thenReturn(countryDB);

        Customer customerDB = new Customer(body, userDB, countryDB);
        when(customerRepo.save(any())).thenReturn(customerDB);

        // body.setUser(user);
        // body.setCountry(country);

        // customerDB.setUser(userDB);
        // customerDB.setCountry(countryDB);

        // Actuar
        CustomerDetails customerRes = customerService.registerCustomer(body);

        // Afirmar
        assertEquals(customerRes.getId(), customerDB.getId());
        verify(customerRepo).existsByEmail(email);
        verify(userService).registerCustomer(body.getUser());
        verify(countryService).findCountryByName(body.getCountry());
        verify(customerRepo).save(any());
    }

    // * updateCustomer
    @Test
    void updateCustomer_PuedeArrojarNotFoundSiNoExisteElCliente() {
        // Arreglar
        Long id = 1l;
        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.updateCustomer(id, null);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(customerRepo).findById(id);
    }

    @Test
    void testUpdate_PuedeArrojarConflictSiYaExisteElEmail() {
        // Arreglar
        Long id = 1l;
        String email = "comidev.contacto@gmail.com";

        CustomerUpdate customerUpdate = new CustomerUpdate();
        customerUpdate.setEmail(email);

        Customer customerDB = new Customer(id);
        customerDB.setEmail("diferente" + email);

        when(customerRepo.findById(id)).thenReturn(Optional.of(customerDB));
        when(customerRepo.existsByEmail(email)).thenReturn(true);

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.updateCustomer(id, customerUpdate);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.CONFLICT, status);
        verify(customerRepo).findById(id);
        verify(customerRepo).existsByEmail(email);
    }

    @Test
    void testUpdate_PuedeActualizarLosDatosDelCliente() {
        // Arreglar
        Long id = 1l;
        String email = "email";
        String countryName = "email";

        CustomerUpdate body = new CustomerUpdate();
        body.setEmail(email);
        body.setCountry(countryName);

        Customer customer = new Customer();
        customer.setEmail(email);

        User user = new User();
        customer.setUser(user);

        Country country = new Country(new CountryCreate(countryName));
        customer.setCountry(country);

        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));
        doNothing().when(userService).updateUser(customer.getUser(), body.getUser());
        when(customerRepo.save(customer)).thenReturn(customer);

        // Actuar
        customerService.updateCustomer(id, body);

        // Afirmar
        verify(customerRepo).findById(id);
        verify(userService).updateUser(customer.getUser(), body.getUser());
        verify(customerRepo).save(customer);
    }
}
