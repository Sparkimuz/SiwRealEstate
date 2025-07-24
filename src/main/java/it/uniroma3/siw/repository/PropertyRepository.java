package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.model.Property;

public interface PropertyRepository extends CrudRepository<Property, Long>{

	public Property findByCity(String city);
	public boolean existsByAddressAndCity(String address, String city);
	public Property findByAddressAndCity(String address, String city);
	public List<Property> findByAgent(Agent agent);
	public List<Property> findAllByCity(String city);
	@Query("""
			SELECT p
			FROM Property p
			WHERE NOT EXISTS (
			      SELECT c
			      FROM Contract c
			      WHERE c.property = p
			        AND c.startDate <= CURRENT_DATE
			        AND (c.finalDate IS NULL OR c.finalDate >= CURRENT_DATE)
			)
			""")
	List<Property> findAllAvailable();
	@Query("""
			SELECT p
			FROM Property p
			WHERE LOWER(p.city) = LOWER(:city)
			  AND NOT EXISTS (
			        SELECT c
			        FROM Contract c
			        WHERE c.property = p
			          AND c.startDate <= CURRENT_DATE
			          AND (c.finalDate IS NULL OR c.finalDate >= CURRENT_DATE)
			  )
			""")
	List<Property> findAvailableByCity(String city);
	@Query("""
			SELECT p
			FROM Property p
			WHERE p.agent = :agent
			  AND NOT EXISTS (
			        SELECT c
			        FROM Contract c
			        WHERE c.property = p
			          AND c.startDate <= CURRENT_DATE
			          AND (c.finalDate   IS NULL OR c.finalDate >= CURRENT_DATE)
			  )
			""")
	List<Property> findAvailableByAgent(Agent agent);

}
