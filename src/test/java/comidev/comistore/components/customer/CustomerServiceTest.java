package comidev.comistore.components.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import comidev.comistore.components.country.CountryRepo;
import comidev.comistore.components.customer.dto.CustomerReq;
import comidev.comistore.components.customer.dto.CustomerRes;
import comidev.comistore.components.customer.dto.CustomerUpdate;
import comidev.comistore.components.user.User;
import comidev.comistore.components.user.UserService;
import comidev.comistore.components.user.dto.UserReq;
import comidev.comistore.exceptions.HttpException;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    private CustomerService customerService;
    @Mock
    private CustomerRepo customerRepo;
    @Mock
    private UserService userService;
    @Mock
    private CountryRepo countryRepo;

    @BeforeEach
    void setUp() {
        this.customerService = new CustomerService(customerRepo, userService, countryRepo);
    }

    @Test
    void testDeleteById_PuedeEliminarPorId() {
        // Arreglar
        Long id = 1l;
        when(customerRepo.existsById(id)).thenReturn(true);

        // Actuar
        customerService.deleteById(id);

        // Afirmar
        verify(customerRepo).existsById(id);
        verify(customerRepo).deleteById(id);
    }

    @Test
    void testDeleteById_PuedeArrojarNotFoundSiNoExiste() {
        // Arreglar
        Long id = 1l;
        when(customerRepo.existsById(id)).thenReturn(false);

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.deleteById(id);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(customerRepo).existsById(id);
    }

    @Test
    void testExistsEmail_PuedeVerificarSiExisteElEmail() {
        // Arreglar
        String email = "comidev.contacto@gmail.com";
        when(customerRepo.existsByEmail(email)).thenReturn(true);

        // Actuar
        customerService.existsEmail(email);

        // Afirmar
        verify(customerRepo).existsByEmail(email);
    }

    @Test
    void testExistsEmail_PuedeArrojarNotFoundSiNoExiste() {
        // Arreglar
        String email = "comidev.contacto@gmail.com";
        when(customerRepo.existsByEmail(email)).thenReturn(false);

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.existsEmail(email);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(customerRepo).existsByEmail(email);
    }

    @Test
    void testFindAll_PuedeDevolverLosClientes() {
        // Arreglar
        Country country = new Country("Perú");
        User user = new User("comidev", "123");
        Customer customer = new Customer(1l);
        customer.setUser(user);
        customer.setCountry(country);
        when(customerRepo.findAll()).thenReturn(List.of(customer));

        // Actuar
        List<CustomerRes> customers = customerService.findAll();

        // Afirmar
        assertEquals(customers.get(0).getId(), new CustomerRes(customer).getId());
        verify(customerRepo).findAll();
    }

    @Test
    void testFindById_PuedeDevolverAlCliente() {
        // Arreglar
        Long id = 1l;
        Country country = new Country("Perú");
        User user = new User("comidev", "123");
        Customer customer = new Customer(id);
        customer.setUser(user);
        customer.setCountry(country);
        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));

        // Actuar
        CustomerRes customerRes = customerService.findById(id);

        // Afirmar
        assertEquals(customerRes.getId(), new CustomerRes(customer).getId());
        verify(customerRepo).findById(id);
    }

    @Test
    void testFindById_PuedeArrojarNotFoundSiNoExiste() {
        // Arreglar
        Long id = 1l;
        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.findById(id);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(customerRepo).findById(id);
    }

    @Test
    void testSave_PuedeArrojarConflictSiElEmailExiste() {
        // Arreglar
        String email = "comidev.contacto@gmail.com";
        CustomerReq customerReq = new CustomerReq();
        customerReq.setEmail(email);
        when(customerRepo.existsByEmail(email)).thenReturn(true);

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.save(customerReq);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.CONFLICT, status);
        verify(customerRepo).existsByEmail(email);
    }

    @Test
    void testSave_PuedeArrojarNotFoundSiElPaisNoExiste() {
        // Arreglar
        String email = "comidev.contacto@gmail.com";
        String country = "Perú";
        UserReq user = new UserReq("comidev", "123");
        User userDB = new User("comidev", "123");
        CustomerReq customerReq = new CustomerReq();
        customerReq.setEmail(email);
        customerReq.setUser(user);
        customerReq.setCountry(country);

        when(customerRepo.existsByEmail(email)).thenReturn(false);
        when(userService.saveCliente(customerReq.getUser())).thenReturn(userDB);
        when(countryRepo.findByName(customerReq.getCountry())).thenReturn(Optional.empty());

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.save(customerReq);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(customerRepo).existsByEmail(email);
        verify(userService).saveCliente(customerReq.getUser());
        verify(countryRepo).findByName(customerReq.getCountry());
    }

    @Test
    void testSave_PuedeGuardarElCliente() {
        // Arreglar
        String email = "comidev.contacto@gmail.com";
        String country = "Perú";
        UserReq user = new UserReq("comidev", "123");

        CustomerReq customerReq = new CustomerReq();
        customerReq.setEmail(email);
        customerReq.setUser(user);
        customerReq.setCountry(country);

        Customer customerDB = new Customer(customerReq);
        User userDB = new User("comidev", "123");
        Country countryDB = new Country(country);
        customerDB.setUser(userDB);
        customerDB.setCountry(countryDB);

        when(customerRepo.existsByEmail(email)).thenReturn(false);
        when(userService.saveCliente(user)).thenReturn(userDB);
        when(countryRepo.findByName(country)).thenReturn(Optional.of(countryDB));
        when(customerRepo.save(any())).thenReturn(customerDB);

        // Actuar
        CustomerRes customerRes = customerService.save(customerReq);

        // Afirmar
        assertEquals(customerRes.getId(), customerDB.getId());
        verify(customerRepo).existsByEmail(email);
        verify(userService).saveCliente(customerReq.getUser());
        verify(countryRepo).findByName(customerReq.getCountry());
        verify(customerRepo).save(any());
    }

    @Test
    void testUpdate_PuedeArrojarNotFoundSiNoExisteElCliente() {
        // Arreglar
        Long id = 1l;
        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            customerService.update(null, id);
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
            customerService.update(customerUpdate, id);
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
        String email = "comidev.contacto@gmail.com";
        String username = "comidev";
        String country = "Perú";

        CustomerUpdate customerUpdate = new CustomerUpdate();
        customerUpdate.setEmail(email);
        customerUpdate.setUsername(username);
        customerUpdate.setCountry(country);

        Customer customerDB = new Customer(id);
        User userDB = new User("antiguo" + username, "123");
        Country countryDB = new Country("antiguo" + country);
        customerDB.setEmail("antiguo" + email);
        customerDB.setUser(userDB);
        customerDB.setCountry(countryDB);

        when(customerRepo.findById(id)).thenReturn(Optional.of(customerDB));
        when(customerRepo.existsByEmail(email)).thenReturn(false);
        when(countryRepo.findByName(country)).thenReturn(Optional.of(new Country(country)));
        when(customerRepo.save(customerDB)).thenReturn(customerDB);

        // Actuar
        CustomerRes customerRes = customerService.update(customerUpdate, id);

        // Afirmar
        assertEquals(customerRes.getId(), customerDB.getId());
        verify(customerRepo).findById(id);
        verify(customerRepo).existsByEmail(email);
        verify(countryRepo).findByName(country);
        verify(customerRepo).save(customerDB);
    }
}
