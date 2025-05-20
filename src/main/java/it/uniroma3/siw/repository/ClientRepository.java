package it.uniroma3.siw.repository;
import it.uniroma3.siw.model.Client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long> {

	public List<Client> findBySurname(String surname);

	public boolean existsByName(String name);
	
	public boolean existsBySurname(String surname);
	
	public boolean existsByBirthdate(LocalDate birthdate);
	
	@Query("SELECT a FROM Agent a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
	public List<Client> findAgents(String name);

	public List<Client> findByNameContainingIgnoreCase(String name);

}
