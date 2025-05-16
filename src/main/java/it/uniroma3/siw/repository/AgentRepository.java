package it.uniroma3.siw.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Agent;

public interface AgentRepository extends CrudRepository<Agent,Long>{
		
		
		public List<Agent> findBySurname(String surname);

		public boolean existsByName(String name);
		
		public boolean existsBySurname(String surname);
		
		public boolean existsByBirthdate(LocalDate birthdate);
		
		@Query("SELECT a FROM Agent a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
		public List<Agent> findAgents(String name);

}
