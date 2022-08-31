package comidev.comistore.components.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import comidev.comistore.components.auth.request.AuthLogin;
import comidev.comistore.components.role.RoleService;
import comidev.comistore.components.role.util.RoleName;
import comidev.comistore.components.user.request.UserCreate;
import comidev.comistore.components.user.request.UserUpdate;
import comidev.comistore.components.user.response.UserDetails;
import comidev.comistore.exceptions.HttpException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bcrypt;

    public List<UserDetails> getAllUsers() {
        return userRepo.findAll().stream()
                .map(UserDetails::new)
                .collect(Collectors.toList());
    }

    public UserDetails getUserById(Long id) {
        return new UserDetails(findUserById(id));
    }

    public User findUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> {
            String message = "No se encuentra el User(id=" + id + ")";
            return new HttpException(HttpStatus.NOT_FOUND, message);
        });
    }

    public boolean existsUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    public UserDetails registerUserAdmin(UserCreate body) {
        return new UserDetails(registerUser(body, RoleName.ADMIN));
    }

    public User registerCustomer(UserCreate body) {
        return registerUser(body, RoleName.CLIENTE);
    }

    private User registerUser(UserCreate body, RoleName roleName) {
        if (userRepo.existsByUsername(body.getUsername())) {
            String message = "El username ya existe!!!";
            throw new HttpException(HttpStatus.CONFLICT, message);
        }

        body.setPassword(bcrypt.encode(body.getPassword()));
        User userNew = new User(body, roleService.initRole(roleName));
        return userRepo.save(userNew);
    }

    public void updateUser(User user, UserUpdate body) {
        String current = body.getCurrentPassword();
        validatePassword(current, user);

        String password = current.equals(body.getNewPassword())
                ? user.getPassword()
                : bcrypt.encode(body.getNewPassword());
        body.setNewPassword(password);

        user.update(body);
        userRepo.save(user);
    }

    public void deleteUser(User user, String password) {
        validatePassword(password, user);
        userRepo.delete(user);
    }

    public User login(AuthLogin body) {
        User userDB = findByUsername(body.getUsername());
        validatePassword(body.getPassword(), userDB);
        return userDB;
    }

    private void validatePassword(String password, User user) {
        if (!bcrypt.matches(password, user.getPassword())) {
            String message = "Credenciales incorrectas";
            throw new HttpException(HttpStatus.UNAUTHORIZED, message);
        }
    }

    private User findByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> {
            String message = "Credenciales incorrectas";
            return new HttpException(HttpStatus.UNAUTHORIZED, message);
        });

    }
}
