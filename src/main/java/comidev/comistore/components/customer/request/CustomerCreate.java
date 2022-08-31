package comidev.comistore.components.customer.request;

import java.sql.Date;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import comidev.comistore.components.customer.util.Gender;
import comidev.comistore.components.user.request.UserCreate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreate {
    @NotEmpty(message = "No puede ser vacio")
    private String name;

    @NotEmpty(message = "No puede ser vacio")
    @Email(message = "No tiene formato de email")
    private String email;

    @NotNull(message = "No puede ser vacio")
    private Gender gender;

    @NotNull(message = "No puede ser vacio")
    private Date dateOfBirth;

    @NotEmpty(message = "No puede ser vacio")
    private String photoUrl;

    @Valid
    @NotNull(message = "No puede ser vacio")
    private UserCreate user;

    @NotEmpty(message = "No puede ser vacio")
    private String country;
}
