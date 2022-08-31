package comidev.comistore.components.auth;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import comidev.comistore.components.auth.request.AuthLogin;
import comidev.comistore.components.role.util.RoleName;
import comidev.comistore.components.user.User;
import comidev.comistore.components.user.UserService;
import comidev.comistore.services.jwt.JwtService;
import comidev.comistore.services.jwt.Payload;
import comidev.comistore.services.jwt.Tokens;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;

    public Tokens login(AuthLogin body) {
        User userDB = userService.login(body);

        List<String> roles = userDB.getRoles().stream()
                .map(item -> item.getName().toString())
                .collect(Collectors.toList());

        return jwtService.createTokens(new Payload(
                userDB.getId(),
                userDB.getUsername(),
                roles));
    }

    public Tokens tokenGenerate(RoleName roleName) {
        Long id = (long) (Math.random() * 1000);
        String username = "Test Swagger: " + UUID.randomUUID().toString();
        List<String> roles = List.of(roleName.toString());
        return jwtService.createTokens(new Payload(id, username, roles));
    }

    public Tokens tokenRefresh(String bearerToken) {
        Payload payload = jwtService.verify(bearerToken);
        return jwtService.createTokens(payload);
    }

    public boolean tokenValidate(String bearerToken) {
        jwtService.verify(bearerToken);
        return true;
    }
}
