package comidev.comistore.components.auth.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthLogin {
    @Size(min = 3)
    @NotEmpty(message = "No puede ser vacío")
    private String username;
    @Size(min = 3)
    @NotEmpty(message = "No puede ser vacío")
    private String password;
}
