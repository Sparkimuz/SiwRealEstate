package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.RealEstateAgency;
import it.uniroma3.siw.repository.RealEstateAgencyRepository;

@Component
public class RealEstateAgencyValidator implements Validator {

    @Autowired
    private RealEstateAgencyRepository agencyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return RealEstateAgency.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RealEstateAgency a = (RealEstateAgency) target;

        /* --- campi obbligatori non vuoti --- */
        if (a.getAddress() == null || a.getAddress().isBlank())
            errors.rejectValue("address", "agency.address.empty");
        if (a.getCity() == null || a.getCity().isBlank())
            errors.rejectValue("city", "agency.city.empty");

        /* --- duplicato (stesso indirizzo + stessa città) --- */
        if (a.getAddress() != null && a.getCity() != null) {
            agencyRepository.findAll().forEach(existing -> {
                boolean sameAddress = existing.getAddress().equalsIgnoreCase(a.getAddress());
                boolean sameCity    = existing.getCity().equalsIgnoreCase(a.getCity());
                boolean notSameId   = a.getId() == null || !a.getId().equals(existing.getId());

                if (sameAddress && sameCity && notSameId) {
                    errors.reject("agency.duplicate");
                }
            });
        }
    }
}
