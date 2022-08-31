package comidev.comistore.components.customer.response;

import java.sql.Date;

import comidev.comistore.components.country.response.CountryDetails;
import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.customer.util.Gender;
import comidev.comistore.components.user.response.UserDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerDetails {
    private Long id;
    private String name;
    private String email;
    private Gender gender;
    private Date dateOfBirth;
    private String photoUrl;
    private UserDetails user;
    private CountryDetails country;

    public CustomerDetails(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.email = customer.getEmail();
        this.gender = customer.getGender();
        this.dateOfBirth = customer.getDateOfBirth();
        this.photoUrl = customer.getPhotoUrl();
        this.user = new UserDetails(customer.getUser());
        this.country = new CountryDetails(customer.getCountry());
    }
}
