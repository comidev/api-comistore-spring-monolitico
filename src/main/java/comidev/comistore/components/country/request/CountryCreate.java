package comidev.comistore.components.country.request;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CountryCreate {
    @NotEmpty(message = "No puede ser vacío")
    private String name;
}
