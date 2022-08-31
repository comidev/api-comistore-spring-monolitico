package comidev.comistore.components.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import comidev.comistore.components.auth.request.AuthLogin;
import comidev.comistore.components.user.User;
import comidev.comistore.components.user.UserService;
import comidev.comistore.components.user.request.UserCreate;
import comidev.comistore.services.jwt.JwtService;
import comidev.comistore.services.jwt.Payload;
import comidev.comistore.services.jwt.Tokens;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private AuthService authService;
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;

    @BeforeEach
    void beforeEach() {
        this.authService = new AuthService(userService, jwtService);
    }

    // * login
    @Test
    void testLogin_PuedeLoguearse() {
        // Arreglar
        String username = "username";
        String password = "password";
        AuthLogin body = new AuthLogin(username, password);
        User user = new User(new UserCreate(username, password), null);
        when(userService.login(body)).thenReturn(user);

        Tokens tokensM = new Tokens("access_token", "refresh_token");
        when(jwtService.createTokens(any())).thenReturn(tokensM);

        // Actuar
        Tokens tokensRes = authService.login(body);

        // Afirmar
        verify(userService).login(body);
        verify(jwtService).createTokens(any());
        assertEquals(tokensM, tokensRes);
    }

    // * tokenRefresh
    @Test
    void testTokenRefresh_PuedeDarmeNuevosTokens() {
        // Arreglar
        String token = "xd";
        Payload payload = new Payload();
        Tokens tokens = new Tokens("access_token",
                "refresh_token");
        when(jwtService.verify(token)).thenReturn(payload);
        when(jwtService.createTokens(payload)).thenReturn(tokens);

        // Actuar
        Tokens tokensRes = authService.tokenRefresh(token);

        // Afirmar
        assertEquals(tokens, tokensRes);
        verify(jwtService).verify(token);
        verify(jwtService).createTokens(payload);
    }

    // * tokenValidate
    @Test
    void testTokenValidate_PuedeValidar() {
        // Arreglar
        String token = "xd";
        when(jwtService.verify(token)).thenReturn(null);

        // Actuar
        authService.tokenValidate(token);

        // Afirmar
        verify(jwtService).verify(token);
    }
}
