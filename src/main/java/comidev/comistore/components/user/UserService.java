package comidev.comistore.components.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import comidev.comistore.components.role.RoleName;
import comidev.comistore.components.role.RoleRepo;
import comidev.comistore.components.user.dto.Passwords;
import comidev.comistore.components.user.dto.UserReq;
import comidev.comistore.components.user.dto.UserRes;
import comidev.comistore.exceptions.HttpException;
import comidev.comistore.services.jwt.JwtService;
import comidev.comistore.services.jwt.Payload;
import comidev.comistore.services.jwt.Tokens;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bcrypt;

    private UserRes adapterUserRes(User item) {
        return new UserRes(item.getUsername());
    }

    private User save(UserReq userReq, RoleName roleName) {
        boolean existsUsername = userRepo.existsByUsername(userReq.getUsername());
        if (existsUsername) {
            String message = "El username ya existe!!!";
            throw new HttpException(HttpStatus.CONFLICT, message);
        }

        String passwordEncode = bcrypt.encode(userReq.getPassword());
        User userNew = new User(userReq.getUsername(), passwordEncode);
        userNew.getRoles().add(roleRepo.findByName(roleName));
        return userRepo.save(userNew);
    }

    public List<UserRes> findAll() {
        List<User> usersDB = userRepo.findAll();
        return usersDB.stream().map(this::adapterUserRes).toList();
    }

    public UserRes saveAdmin(UserReq userReq) {
        return adapterUserRes(save(userReq, RoleName.ADMIN));
    }

    public User saveCliente(UserReq userReq) {
        return save(userReq, RoleName.CLIENTE);
    }

    public boolean existsUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    public boolean updatePassword(Long id, Passwords passwords) {
        User userDB = userRepo.findById(id)
                .orElseThrow(() -> {
                    String message = "El usuario con es id no existe!!!!";
                    throw new HttpException(HttpStatus.NOT_FOUND, message);
                });

        boolean passwordIsCorrect = bcrypt
                .matches(passwords.getCurrentPassword(), userDB.getPassword());

        if (!passwordIsCorrect) {
            String message = "Password incorrecto!!!";
            throw new HttpException(HttpStatus.UNAUTHORIZED, message);
        }

        userDB.setPassword(passwords.getNewPassword());
        userRepo.save(userDB);
        return true;
    }

    private User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    String message = "Credenciales incorrectas";
                    return new HttpException(HttpStatus.UNAUTHORIZED, message);
                });

    }

    public Tokens login(UserReq userReq) {
        User userDB = findByUsername(userReq.getUsername());
        String passwordDB = userDB.getPassword();

        if (!bcrypt.matches(userReq.getPassword(), passwordDB)) {
            String message = "Credenciales incorrectas";
            throw new HttpException(HttpStatus.UNAUTHORIZED, message);
        }

        List<String> roles = userDB.getRoles().stream()
                .map(item -> item.getName().toString())
                .toList();

        return jwtService.createTokens(new Payload(
                userDB.getId(),
                userDB.getUsername(),
                roles));
    }

    public Tokens tokenRefresh(String bearerToken) {
        Payload payload = jwtService.verify(bearerToken);
        return jwtService.createTokens(payload);
    }

    public void tokenValidate(String bearerToken) {
        jwtService.verify(bearerToken);
    }

    public void updateUsername(String usernamePrev, String usernameNew) {
        if (userRepo.existsByUsername(usernameNew)) {
            String message = "El nuevo username ya existe!!";
            throw new HttpException(HttpStatus.CONFLICT, message);
        }
        User userDB = findByUsername(usernamePrev);
        userDB.setUsername(usernameNew);
    }
}
