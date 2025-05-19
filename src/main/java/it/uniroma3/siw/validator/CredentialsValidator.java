package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;

@Component
public class CredentialsValidator implements Validator {

    private static final int MIN_PW_LEN = 8;

    @Autowired
    private CredentialsService credentialsService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Credentials.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Credentials c = (Credentials) target;

        /* --- lunghezza password --- */
        if (c.getPassword() == null || c.getPassword().length() < MIN_PW_LEN) {
            errors.rejectValue("password", "credentials.password.tooShort");
        }

        /* --- duplicato username --- */
        Credentials existing = credentialsService.getCredentials(c.getUsername());
        if (existing != null &&
           (c.getId() == null || !existing.getId().equals(c.getId()))) {
            errors.rejectValue("username", "credentials.username.duplicate");
        }
    }
}