package comidev.comistore.components.country.dto;

import comidev.comistore.components.country.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CountryRes {
    private String name;

    public CountryRes(Country country) {
        this.name = country.getName();
    }
}
