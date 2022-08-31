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

import comidev.comistore.components.auth.request.AuthLogin;
import comidev.comistore.components.role.RoleService;
import comidev.comistore.components.role.util.RoleName;
import comidev.comistore.components.user.request.UserCreate;
import comidev.comistore.components.user.response.UserDetails;
import comidev.comistore.exceptions.HttpException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private RoleService roleService;
    @Mock
    private BCryptPasswordEncoder bcrypt;

    @BeforeEach
    void beforeEach() {
        this.userService = new UserService(userRepo, roleService, bcrypt);
    }

    // * findAll
    @Test
    void testFindAll_puedeDevolverLosUsuarios() {
        String username = "comidev";
        User user = new User();
        user.setUsername(username);
        when(userRepo.findAll()).thenReturn(List.of(user));

        List<UserDetails> users = userService.getAllUsers();

        verify(userRepo).findAll();
        assertEquals(user.getUsername(), users.get(0).getUsername());
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

    // * registerUserAdmin
    @Test
    void testSaveAdmin_puedeGuardarUnUsuarioAdmin() {
        // Arreglar
        String username = "comidev";
        String password = "passstrong";
        UserCreate body = new UserCreate(username, password);
        when(userRepo.existsByUsername(username)).thenReturn(false);
        when(bcrypt.encode(password)).thenReturn(password);
        when(roleService.initRole(RoleName.ADMIN)).thenReturn(null);

        User user = new User(body, null);
        when(userRepo.save(user)).thenReturn(user);

        // Actuar
        userService.registerUserAdmin(body);

        // Afirmar
        verify(userRepo).existsByUsername(body.getUsername());
        verify(bcrypt).encode(body.getPassword());
        verify(roleService).initRole(RoleName.ADMIN);
        verify(userRepo).save(user);
    }

    @Test
    void testSaveAdmin_throwHttpExceptionSiExisteElUsername() {
        // Arreglar
        UserCreate body = new UserCreate();
        when(userRepo.existsByUsername(body.getUsername())).thenReturn(true);

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            userService.registerUserAdmin(body);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.CONFLICT, status);
        verify(userRepo).existsByUsername(body.getUsername());
        verify(bcrypt, never()).encode(any());
        verify(roleService, never()).initRole(any());
        verify(userRepo, never()).save(any());
    }

    // * login
    @Test
    void testLogin_PuedeLoguearse() {
        // Arreglar
        String username = "username";
        String password = "password";
        AuthLogin userReq = new AuthLogin(username, password);
        User userDB = new User(new UserCreate(username, password), null);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userDB));
        when(bcrypt.matches(password, userDB.getPassword())).thenReturn(true);

        // Actuar
        User response = userService.login(userReq);

        // Afirmar
        assertEquals(userDB, response);
        verify(userRepo).findByUsername(username);
        verify(bcrypt).matches(password, password);
    }

    @Test
    void testLogin_ThrowUnAuthorizedCuandoElUsernameEsIncorrecto() {
        // Arreglar
        String username = "username";
        String password = "password";
        AuthLogin userReq = new AuthLogin(username, password);
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            userService.login(userReq);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.UNAUTHORIZED, status);
        verify(userRepo).findByUsername(username);
        verify(bcrypt, never()).matches(any(), any());
    }

    @Test
    void testLogin_ThrowUnAuthorizedCuandoElPasswordEsIncorrecto() {
        // Arreglar
        String username = "username";
        String password = "password";
        AuthLogin userReq = new AuthLogin(username, password);
        User userDB = new User(new UserCreate(username, password), null);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userDB));
        when(bcrypt.matches(password, userDB.getPassword())).thenReturn(false);

        // Actuar
        HttpStatus status = assertThrows(HttpException.class, () -> {
            userService.login(userReq);
        }).getStatus();

        // Afirmar
        assertEquals(HttpStatus.UNAUTHORIZED, status);
        verify(userRepo).findByUsername(username);
        verify(bcrypt).matches(password, userDB.getPassword());
    }
}
