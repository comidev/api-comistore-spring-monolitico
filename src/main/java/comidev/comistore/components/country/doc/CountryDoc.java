package comidev.comistore.components.country.doc;

import java.util.List;

import comidev.comistore.components.country.response.*;
import io.swagger.v3.oas.annotations.Operation;

public interface CountryDoc {
    @Operation(summary = "Devuelve lista de paises")
    public List<CountryDetails> getAllCountries();
}
