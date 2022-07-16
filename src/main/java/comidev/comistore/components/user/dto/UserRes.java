package comidev.comistore.components.user.dto;

import lombok.Getter;

@Getter
public class UserRes {
    private String username;

    public UserRes() {
    }

    public UserRes(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserRes other = (UserRes) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
}
