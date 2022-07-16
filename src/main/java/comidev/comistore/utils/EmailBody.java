package comidev.comistore.utils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailBody {
    @NotEmpty(message = "No puede ser vacio")
    @Email(message = "Debe tener formato email")
    private String email;
}
