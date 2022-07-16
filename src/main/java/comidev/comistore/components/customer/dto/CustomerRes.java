package comidev.comistore.components.customer.dto;

import java.sql.Date;

import comidev.comistore.components.customer.Customer;
import comidev.comistore.components.customer.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerRes {
    private Long id;
    private String name;
    private String email;
    private Gender gender;
    private Date dateOfBirth;
    private String photoUrl;
    private String username;
    private String country;

    public CustomerRes(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.email = customer.getEmail();
        this.gender = customer.getGender();
        this.dateOfBirth = customer.getDateOfBirth();
        this.photoUrl = customer.getPhotoUrl();
        this.username = customer.getUser().getUsername();
        this.country = customer.getCountry().getName();
    }
}
