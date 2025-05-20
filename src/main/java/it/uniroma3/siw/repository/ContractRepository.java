package it.uniroma3.siw.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Contract;
import it.uniroma3.siw.model.Agent;

public interface ContractRepository extends CrudRepository<Contract, Long> {
    List<Contract> findByAgent(Agent agent);
    List<Contract> findByClientId(Long clientId);
    List<Contract> findByPropertyId(Long propertyId);
	boolean existsOverlappingContract(Long id, LocalDate startDate, LocalDate finalDate);
}
