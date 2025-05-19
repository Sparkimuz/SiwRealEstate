package it.uniroma3.siw.validator;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.service.AgentService;

@Component
public class AgentValidator implements Validator {

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = Year.now().getValue();

    @Autowired
    private AgentService agentService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Agent.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Agent a = (Agent) target;

        /* --- duplicato (nome + cognome + birthdate) --- */
        if (a.getName() != null && a.getSurname() != null && a.getBirthdate() != null) {
            boolean exists = agentService.existsByNameSurnameBirth(a);
            // evita falso positivo se sto aggiornando lo stesso record
            if (exists && (a.getId() == null)) {
                errors.reject("agent.duplicate");
            }
        }

        /* --- range anno di nascita --- */
        if (a.getBirthdate() != null) {
            int year = a.getBirthdate().getYear();
            if (year < MIN_YEAR || year > MAX_YEAR) {
                errors.rejectValue("birthdate", "agent.birthdate.invalid");
            }
        }
    }
}
