package comidev.comistore.components.user.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Passwords {
    @Size(min = 3)
    @NotEmpty(message = "No puede ser vacio uu")
    private String newPassword;
    @Size(min = 3)
    @NotEmpty(message = "No puede ser vacio uu")
    private String currentPassword;
}
