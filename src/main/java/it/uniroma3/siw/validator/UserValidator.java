package it.uniroma3.siw.validator;

import java.time.Year;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.User;

@Component
public class UserValidator implements Validator {

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = Year.now().getValue(); // sempre aggiornato

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User u = (User) target;

        if (u.getBirthdate() != null) {
            int year = u.getBirthdate().getYear();
            if (year < MIN_YEAR || year > MAX_YEAR) {
                errors.rejectValue("birthdate", "user.birthdate.invalid");
            }
        }
    }
}
