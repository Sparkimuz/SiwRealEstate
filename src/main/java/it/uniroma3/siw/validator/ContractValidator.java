package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Contract;
import it.uniroma3.siw.service.ContractService;

@Component
public class ContractValidator implements Validator {

    @Autowired
    private ContractService contractService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Contract.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Contract c = (Contract) target;

        /* data inizio < data fine */
        if (c.getStartDate() != null && c.getFinalDate() != null &&
            !c.getStartDate().isBefore(c.getFinalDate())) {
            errors.rejectValue("finalDate", "contract.dates.invalid",
                               "La data di fine deve essere successiva a quella di inizio");
        }

        /* prezzo > 0 */
        if (c.getFinalPrice() <= 0) {
            errors.rejectValue("finalPrice", "contract.price.invalid",
                               "Il prezzo deve essere positivo");
        }

        /* contratto duplicato per stesso immobile e stesso periodo */
        if (c.getProperty() != null &&
            contractService.existsSameContract(
                c.getProperty().getId(), c.getStartDate(), c.getFinalDate()) &&
            // evita falso positivo in update
            (c.getId() == null)
        ) {
            errors.reject("contract.duplicate",
                          "Esiste già un contratto per questo immobile in quelle date");
        }
    }
}