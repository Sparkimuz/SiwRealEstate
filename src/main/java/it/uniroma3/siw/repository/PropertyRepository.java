package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Property;

public interface PropertyRepository extends CrudRepository<Property, Long>{
	
	boolean existsByName(String name);
	
	public Property findByCity(String city);
	public Property findByType(String type);
	
	
	@Query("SELECT p FROM Property p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
	public List<Property> findProperties(String name);
	

}
