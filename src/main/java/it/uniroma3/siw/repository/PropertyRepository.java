package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.model.Property;

public interface PropertyRepository extends CrudRepository<Property, Long>{
		
	public Property findByCity(String city);
	public Property findByType(String type);
	public boolean existsByAddressAndCity(String address, String city);
	public Property findByAddressAndCity(String address, String city);
	public List<Property> findByAgent(Agent agent);

}
