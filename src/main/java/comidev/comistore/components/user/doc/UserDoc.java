package comidev.comistore.components.user.doc;

import java.util.List;

import comidev.comistore.components.user.request.*;
import comidev.comistore.components.user.response.*;
import io.swagger.v3.oas.annotations.Operation;

public interface UserDoc {
    @Operation(summary = " Devuelve lista de usuarios")
    List<UserDetails> getAllUsers();

    @Operation(summary = "Registra un usuario admin")
    UserDetails registerUserAdmin(UserCreate userReq);

    @Operation(summary = "Verifica si el username se encuentra registrado")
    boolean existsUsername(String username);
}
