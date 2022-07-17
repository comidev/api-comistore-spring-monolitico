package comidev.comistore.components.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import comidev.comistore.components.role.Role;
import comidev.comistore.components.role.RoleName;
import comidev.comistore.components.role.RoleRepo;
import comidev.comistore.components.user.dto.Passwords;
import comidev.comistore.components.user.dto.UserReq;
import comidev.comistore.components.user.dto.UserRes;
import comidev.comistore.exceptions.HttpException;
import comidev.comistore.services.jwt.JwtService;
import comidev.comistore.services.jwt.Payload;
import comidev.comistore.services.jwt.Tokens;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private RoleRepo roleRepo;
    @Mock
    private JwtService jwtService;
    @Mock
    private BCryptPasswordEncoder bcrypt;

    private UserService userService;

    @BeforeEach
    void beforeEach() {
        this.userService = new UserService(userRepo, roleRepo, jwtService, bcrypt);
        roleRepo.deleteAll();
        roleRepo.save(new Role(RoleName.ADMIN));
    }

    // * findAll
    @Test
    void testFindAll_puedeDevolverLosUsuarios() {
        String username = "comidev";
        User user = new User();
        user.setUsername(username);
        when(userRepo.findAll()).thenReturn(List.of(user));

        List<UserRes> users = userService.findAll();

        verify(userRepo).findAll();
        assertEquals(user.getUsername(), users.get(0).getUsername());
    }

    // * saveAdmin
    @Test
    void testSaveAdmin_puedeGuardarUnUsuarioAdmin() {
        // Arreglar
        UserReq userReq = new UserReq();

        User userDB = new User(userReq.getUsername(), userReq.getPassword());
        Role roleDB = new Role();
        userDB.getRoles().add(roleDB);

        when(userRepo.existsByUsername(userReq.getUsername())).thenReturn(false);
        when(bcrypt.encode(userReq.getPassword())).thenReturn(userReq.getPassword());
        when(roleRepo.findByName(RoleName.ADMIN)).thenReturn(roleDB);
        when(userRepo.save(userDB)).thenReturn(userDB);

        // Actuar
        userService.saveAdmin(userReq);

        // Afirmar
        verify(userRepo).existsByUsername(userReq.getUsername());
        verify(bcrypt).encode(userReq.getPassword());
        verify(roleRepo).findByName(RoleName.ADMIN);
        verify(userRepo).save(userDB);
    }

    @Test
    void testSaveAdmin_throwHttpExceptionSiExisteElUsername() {
        // Arreglar
        UserReq userReq = new UserReq();

        when(userRepo.existsByUsername(userReq.getUsername())).thenReturn(true);

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            userService.saveAdmin(userReq);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.CONFLICT, status);
        verify(userRepo).existsByUsername(userReq.getUsername());
        verify(bcrypt, never()).encode(any());
        verify(roleRepo, never()).findByName(any());
        verify(userRepo, never()).save(any());
    }
    // * existsUsername
    @Test
    void testExistsUsername_PuedeVerificarSiExisteElUsuario() {
        // Arreglar
        String username = "username";

        when(userRepo.existsByUsername(username)).thenReturn(true);

        // Actuar
        boolean response = userService.existsUsername(username);

        // Afirmar
        assertTrue(response);
        verify(userRepo).existsByUsername(username);
    }

    // * updatePassword
    @Test
    void testUpdatePassword_PuedeActualizarElPassword() {
        // Arreglar
        Long id = 1l;
        Passwords passwords = new Passwords("nuevo", "actual");
        User userDB = new User("username", passwords.getCurrentPassword());

        when(userRepo.findById(id)).thenReturn(Optional.of(userDB));
        when(bcrypt.matches(passwords.getCurrentPassword(), userDB.getPassword()))
            .thenReturn(true);

        // Actuar
        boolean response = userService.updatePassword(id, passwords);

        // Afirmar
        assertTrue(response);
        verify(userRepo).findById(id);
        verify(bcrypt)
        .matches(passwords.getCurrentPassword(), passwords.getCurrentPassword());
        verify(userRepo).save(userDB);
    }

    @Test
    void testUpdatePassword_ThrowNotFoundSiElIdNoExiste() {
        // Arreglar
        Long id = 1l;
        Passwords passwords = new Passwords("nuevo", "actual");
        when(userRepo.findById(id)).thenReturn(Optional.empty());

        // Actuar

        // Afirmar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            userService.updatePassword(id, passwords);
        }).getStatus();
        assertEquals(HttpStatus.NOT_FOUND, status);
        verify(userRepo).findById(id);
        verify(bcrypt, never()).matches(any(), any());
        verify(userRepo, never()).save(any());
    }

    @Test
    void testUpdatePassword_ThrowUnAuthorizedSiLosPasswordsNoSonIguales() {
        // Arreglar
        Long id = 1l;
        Passwords passwords = new Passwords("nuevo", "actual");
        User userDB = new User("username", passwords.getCurrentPassword());
        when(userRepo.findById(id)).thenReturn(Optional.of(userDB));

        // Actuar

        // Afirmar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            userService.updatePassword(id, passwords);
        }).getStatus();
        assertEquals(HttpStatus.UNAUTHORIZED, status);
        verify(userRepo).findById(id);
        verify(bcrypt).matches(passwords.getCurrentPassword(), passwords.getCurrentPassword());
        verify(userRepo, never()).save(any());
    }

    // * login
    @Test
    void testLogin_PuedeLoguearse() {
        // Arreglar
        String username = "username";
        String password = "password";
        UserReq userReq = new UserReq(username, password);
        User userDB = new User(username, password);
        Long id = 1l;
        userDB.setId(id);
        Tokens tokensM = new Tokens("access_token", "refresh_token");
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userDB));
        when(bcrypt.matches(password, password)).thenReturn(true);
        when(jwtService.createTokens(any())).thenReturn(tokensM);

        // Actuar
        Tokens tokensRes = userService.login(userReq);

        // Afirmar
        assertEquals(tokensM, tokensRes);
        verify(userRepo).findByUsername(username);
        verify(bcrypt).matches(password, password);
        verify(jwtService).createTokens(any());
    }

    @Test
    void testLogin_ThrowUnAuthorizedCuandoElUsernameEsIncorrecto() {
        // Arreglar
        String username = "username";
        String password = "password";
        UserReq userReq = new UserReq(username, password);
        User userDB = new User(username, password);
        Long id = 1l;
        userDB.setId(id);
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // Actuar

        // Afirmar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            userService.login(userReq);
        }).getStatus();
        assertEquals(HttpStatus.UNAUTHORIZED, status);
        verify(userRepo).findByUsername(username);
        verify(bcrypt, never()).matches(any(), any());
        verify(jwtService, never()).createTokens(any());

    }

    @Test
    void testLogin_ThrowUnAuthorizedCuandoElPasswordEsIncorrecto() {
        // Arreglar
        String username = "username";
        String password = "password";
        UserReq userReq = new UserReq(username, password);
        User userDB = new User(username, password);
        Long id = 1l;
        userDB.setId(id);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userDB));
        when(bcrypt.matches(password, password)).thenReturn(false);

        // Actuar

        // Afirmar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            userService.login(userReq);
        }).getStatus();
        assertEquals(HttpStatus.UNAUTHORIZED, status);
        verify(userRepo).findByUsername(username);
        verify(bcrypt).matches(password, password);
        verify(jwtService, never()).createTokens(any());
    }

    // * tokenRefresh
    @Test
    void testTokenRefresh_PuedeDarmeNuevosTokens() {
        // Arreglar
        String token = "xd";
        Payload payload = new Payload();
        Tokens tokens = new Tokens("access_token", "refresh_token");
        when(jwtService.verify(token)).thenReturn(payload);
        when(jwtService.createTokens(payload)).thenReturn(tokens);

        // Actuar
        Tokens tokensRes = userService.tokenRefresh(token);

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
        userService.tokenValidate(token);

        // Afirmar
        verify(jwtService).verify(token);
    }
}
