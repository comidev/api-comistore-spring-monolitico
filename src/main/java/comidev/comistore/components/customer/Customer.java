package comidev.comistore.components.customer;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import comidev.comistore.components.country.Country;
import comidev.comistore.components.customer.dto.CustomerReq;
import comidev.comistore.components.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customers")
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private Gender gender;
    @Column(nullable = false)
    private Date dateOfBirth;
    @Column(nullable = false)
    private String photoUrl;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public Customer(Long id) {
        this.id = id;
    }

    public Customer(String name, String email, Gender gender, Date dateOfBirth,
            String photoUrl, User user, Country country) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.photoUrl = photoUrl;
        this.user = user;
        this.country = country;
    }

    public Customer(CustomerReq customerReq) {
        this.name = customerReq.getName();
        this.email = customerReq.getEmail();
        this.gender = customerReq.getGender();
        this.dateOfBirth = customerReq.getDateOfBirth();
        this.photoUrl = customerReq.getPhotoUrl();
    }
}
