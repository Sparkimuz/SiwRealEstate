package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.model.Contract;
import it.uniroma3.siw.repository.ContractRepository;

public class ContractService {

	public boolean existsSameContract(Long id, LocalDate startDate, LocalDate finalDate) {
	    return this.contractRepository.existsOverlappingContract(id, startDate, finalDate);
	}
	@Autowired
    private ContractRepository contractRepository;

    public Contract findById(Long id) {
        return this.contractRepository.findById(id).orElse(null);
    }

    public List<Contract> findAll() {
        return (List<Contract>) this.contractRepository.findAll();
    }

    public List<Contract> findByAgent(Agent agent) {
        return this.contractRepository.findByAgent(agent);
    }

    public Contract save(Contract contract) {
        return this.contractRepository.save(contract);
    }

    public void delete(Contract contract) {
        this.contractRepository.delete(contract);
    }

}
