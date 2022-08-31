package comidev.comistore.components.auth.doc;

import comidev.comistore.components.auth.request.AuthLogin;
import comidev.comistore.components.role.util.RoleName;
import comidev.comistore.services.jwt.Tokens;
import io.swagger.v3.oas.annotations.Operation;

public interface AuthDoc {
    @Operation(summary = "Devuelve tokens por username y password")
    Tokens login(AuthLogin body);

    @Operation(summary = "Genera tokens por Rol, solo para probar en Swagger")
    Tokens tokenGenerate(RoleName role);

    @Operation(summary = "Devuelve tokens por Token Refresh")
    Tokens tokenRefresh(String bearerToken);

    @Operation(summary = "Verifica si el token es valido")
    boolean tokenValidate(String bearerToken);
}
