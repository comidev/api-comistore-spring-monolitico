package comidev.comistore.components.user.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserReq {
    @Size(min = 3)
    @NotEmpty(message = "No puede ser vacio :(")
    private String username;
    @Size(min = 3)
    @NotEmpty(message = "No puede ser vacio :(")
    private String password;

    public UserReq(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
