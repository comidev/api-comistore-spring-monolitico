package comidev.comistore.components.country.response;

import comidev.comistore.components.country.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CountryDetails {
    private Long id;
    private String name;

    public CountryDetails(Country entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
