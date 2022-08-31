package comidev.comistore.components.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import comidev.comistore.components.user.request.UserCreate;
import comidev.comistore.config.RepoUnitTest;

@RepoUnitTest
public class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    // * existsByUsername
    @Test
    void FALSE_CuandoNoExisteElUserame_existsByUsername() {
        String username = "comidev";

        boolean response = userRepo.existsByUsername(username);

        assertFalse(response);
    }

    @Test
    void TRUE_CuandoExisteElUserame_existsByUsername() {
        String username = "comidev";
        userRepo.save(new User(new UserCreate(username, "password"), null));

        boolean response = userRepo.existsByUsername(username);

        assertTrue(response);
    }

    // * findByUsername
    @Test
    void IS_EMPTY_CuandoNoExisteElUsuario_findByUsername() {
        String username = "comidev";

        Optional<User> response = userRepo.findByUsername(username);

        assertTrue(response.isEmpty());
        assertNull(response.orElse(null));
    }

    @Test
    void IS_PRESENT_CuandoExisteElUsuario_findByUsername() {
        String username = "comidev";
        User user = userRepo.save(new User(new UserCreate(username, "password"), null));

        Optional<User> response = userRepo.findByUsername(username);

        assertTrue(response.isPresent());
        assertNotNull(response.get());
        assertSame(response.get(), user);
    }
}
