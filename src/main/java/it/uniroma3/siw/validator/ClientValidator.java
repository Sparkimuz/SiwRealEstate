package it.uniroma3.siw.validator;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Client;
import it.uniroma3.siw.service.ClientService;

@Component
public class ClientValidator implements Validator {

    /* intervallo ammesso per l’anno di nascita */
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = Year.now().getValue();

    @Autowired
    private ClientService clientService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Client.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Client c = (Client) target;

        /* -------- controllo duplicato (nome + cognome + data di nascita) -------- */
        if (c.getName() != null && c.getSurname() != null && c.getBirthdate() != null) {
            boolean exists = clientService.existsByNameSurnameBirth(c);
            /* evita falso positivo quando si aggiorna lo stesso record */
            if (exists && (c.getId() == null)) {
                errors.reject("client.duplicate");
            }
        }

        /* -------- validazione data di nascita -------- */
        if (c.getBirthdate() != null) {
            int year = c.getBirthdate().getYear();
            if (year < MIN_YEAR || year > MAX_YEAR) {
                errors.rejectValue("birthdate", "client.birthdate.invalid");
            }
        }
    }
}
