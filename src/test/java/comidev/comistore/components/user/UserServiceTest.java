package comidev.comistore.components.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
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

    @Test
    void testFindAll_puedeDevolverLosUsuarios() {
        when(userRepo.findAll()).thenReturn(List.of(new User("comidev", "123")));

        List<UserRes> users = userService.findAll();

        verify(userRepo).findAll();
        assertEquals(new UserRes("comidev"), users.get(0));
    }

    @Test
    void testSaveAdmin_puedeGuardarUnUsuarioAdmin() {
        // Arreglar
        String password = "password";
        User userDB = new User("username", password);
        Role roleDB = new Role();
        userDB.getRoles().add(roleDB);
        when(bcrypt.encode(password)).thenReturn(password);
        when(roleRepo.findByName(RoleName.ADMIN)).thenReturn(roleDB);
        when(userRepo.save(userDB)).thenReturn(userDB);

        // Actuar
        UserReq userReq = new UserReq("username", password);
        userService.saveAdmin(userReq);

        // Afirmar
        verify(bcrypt).encode(password);
        verify(userRepo).save(userDB);
    }

    @Test
    void testSaveAdmin_throwHttpExceptionSiExisteElUsername() {
        // Arreglar
        String username = "username";
        when(userRepo.existsByUsername(username)).thenReturn(true);

        // Actuar
        UserReq userReq = new UserReq(username, "password");

        // Afirmar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            userService.saveAdmin(userReq);
        }).getStatus();
        assertEquals(HttpStatus.CONFLICT, status);
        verify(userRepo).existsByUsername(username);
        verify(bcrypt, never()).encode(any());
        verify(roleRepo, never()).findByName(any());
        verify(userRepo, never()).save(any());
    }

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

    @Test
    void testUpdatePassword_PuedeActualizarElPassword() {
        // Arreglar
        Long id = 1l;
        Passwords passwords = new Passwords("nuevo", "actual");
        User userDB = new User("username", passwords.getCurrentPassword());
        when(userRepo.findById(id)).thenReturn(Optional.of(userDB));
        when(bcrypt.matches(passwords.getCurrentPassword(), passwords.getCurrentPassword()))
                .thenReturn(true);

        // Actuar
        boolean response = userService.updatePassword(id, passwords);

        // Afirmar
        assertTrue(response);
        verify(userRepo).findById(id);
        verify(bcrypt).matches(passwords.getCurrentPassword(), passwords.getCurrentPassword());
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
