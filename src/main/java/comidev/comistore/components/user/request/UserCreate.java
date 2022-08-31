package comidev.comistore.components.user.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreate {
    @Size(min = 3)
    @NotEmpty(message = "No puede ser vacio :(")
    private String username;
    @Size(min = 3)
    @NotEmpty(message = "No puede ser vacio :(")
    private String password;
}
