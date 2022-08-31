package comidev.comistore.components.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import comidev.comistore.components.role.Role;
import comidev.comistore.components.user.request.UserCreate;
import comidev.comistore.components.user.request.UserUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany()
    @JoinTable(name = "user_role", joinColumns = {
            @JoinColumn(referencedColumnName = "id", name = "user_id")
    }, inverseJoinColumns = {
            @JoinColumn(referencedColumnName = "id", name = "role_id")
    })
    private Set<Role> roles;

    public User(UserCreate dto, Set<Role> roles) {
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.roles = roles != null ? roles : new HashSet<>();
    }

    public void update(UserUpdate dto) {
        this.username = dto.getUsername();
        this.password = dto.getNewPassword();
    }
}
