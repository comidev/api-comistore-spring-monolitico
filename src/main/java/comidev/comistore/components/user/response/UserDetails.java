package comidev.comistore.components.user.response;

import comidev.comistore.components.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDetails {
    private String username;

    public UserDetails(User entity) {
        this.username = entity.getUsername();
    }
}
