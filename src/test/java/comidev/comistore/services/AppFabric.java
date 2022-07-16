package comidev.comistore.services;

import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import comidev.comistore.components.category.Category;
import comidev.comistore.components.category.CategoryRepo;
import comidev.comistore.components.country.Country;
import comidev.comistore.components.country.CountryRepo;
import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.customer.CustomerRepo;
import comidev.comistore.components.customer.Gender;
import comidev.comistore.components.invoice.Invoice;
import comidev.comistore.components.invoice.InvoiceRepo;
import comidev.comistore.components.invoice_item.InvoiceItem;
import comidev.comistore.components.invoice_item.InvoiceItemRepo;
import comidev.comistore.components.product.Product;
import comidev.comistore.components.product.ProductRepo;
import comidev.comistore.components.role.RoleRepo;
import comidev.comistore.components.user.User;
import comidev.comistore.components.user.UserRepo;
import comidev.comistore.services.jwt.JwtService;
import comidev.comistore.services.jwt.Payload;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
@Getter
@AllArgsConstructor
public class AppFabric {
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bcrypt;
    private final CategoryRepo categoryRepo;
    private final CountryRepo countryRepo;
    private final ProductRepo productRepo;
    private final CustomerRepo customerRepo;
    private final RoleRepo roleRepo;
    private final InvoiceItemRepo invoiceItemRepo;
    private final InvoiceRepo invoiceRepo;

    private String generate() {
        return UUID.randomUUID().toString();
    }

    public User createUser(String username, String password) {
        String usernameDB = username != null ? username : generate();
        String passwordDB = password != null ? bcrypt.encode(password) : "comidev123";
        return userRepo.save(new User(usernameDB, passwordDB));
    }

    public String createToken(String... roles) {
        List<String> rolesName = roles.length > 0 ? List.of(roles) : List.of("ADMIN");
        String token = jwtService
                .createTokens(new Payload(1l, "comidev", rolesName))
                .getAccess_token();
        return "Bearer " + token;
    }

    public Category createCategory(String name) {
        String nameDB = name != null ? name : "Tecnologia";
        return categoryRepo.save(categoryRepo.findByName(nameDB)
                .orElse(new Category(nameDB)));
    }

    public Country createCountry(String name) {
        String nameDB = name != null ? name : "Perú";
        return countryRepo.save(countryRepo.findByName(nameDB)
                .orElse(new Country(nameDB)));
    }

    public Product createProduct(String name) {
        String nameDB = name != null ? name : "name";
        return productRepo.save(new Product(nameDB, "x",
                "x", 10, 3.5f));
    }

    public Customer createCustomer(String email, User user, Country country) {
        String emailDB = email != null ? email : (generate() + "@gmail.com");
        User userDB = user != null ? user : createUser(null, null);
        Country countryDB = country != null ? country : createCountry(null);

        return customerRepo.save(new Customer("x", emailDB, Gender.MALE,
                Date.valueOf(LocalDate.now()), "X",
                userDB, countryDB));
    }

    public InvoiceItem createInvoiceItem(Product product) {
        Product productDB = product != null ? product : createProduct(null);
        return invoiceItemRepo.save(new InvoiceItem(3, productDB));
    }

    public Invoice createInvoice(Customer customer, InvoiceItem invoiceItem) {
        Customer customerDB = customer != null
                ? customer
                : createCustomer(null, null, null);
        List<InvoiceItem> invoiceItemsDB = List.of(invoiceItem != null
                ? invoiceItem
                : createInvoiceItem(null));

        return invoiceRepo.save(new Invoice("x", 10f,
                customerDB, invoiceItemsDB));
    }
}
