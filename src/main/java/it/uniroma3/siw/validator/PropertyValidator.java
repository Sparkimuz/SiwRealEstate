package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Property;
import it.uniroma3.siw.repository.PropertyRepository;

@Component
public class PropertyValidator implements Validator {

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Property.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Property p = (Property) target;

        /* --- controllo duplicato (stesso indirizzo + città) --- */
        boolean alreadyExists =
            propertyRepository.existsByAddressAndCity(
                p.getAddress(), p.getCity());

        /* Se sto salvando una nuova Property (id null)            *
         * o ne sto modificando una e l'id è diverso da quello già *
         * presente, segnalo l’errore                              */
        if (alreadyExists && (p.getId() == null ||
            !propertyRepository.findByAddressAndCity(
                p.getAddress(), p.getCity()).getId().equals(p.getId()))) {
            errors.reject("property.duplicate");
        }

        /* --- eventuali controlli di consistenza aggiuntivi --- */
        if (p.getPrice() <= 0)
            errors.rejectValue("price", "property.price.invalid");
        if (p.getSize() <= 0)
            errors.rejectValue("size", "property.size.invalid");
    }
}
