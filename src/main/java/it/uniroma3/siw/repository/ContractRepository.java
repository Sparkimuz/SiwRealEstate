package it.uniroma3.siw.repository;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Contract;

public interface ContractRepository extends CrudRepository<Contract, Long>{
}
