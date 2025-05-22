package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.model.Property;
import it.uniroma3.siw.repository.PropertyRepository;

@Service
public class PropertyService {

	@Autowired
	private PropertyRepository propertyRepository;
	
	public Property findById(Long id) {
		return propertyRepository.findById(id).get();
	}
	
	public Iterable<Property> findAll(){
		return propertyRepository.findAll();
	}
	
	public void save(Property p) {
		propertyRepository.save(p);
	}
	
	public void delete(Property p) {
		propertyRepository.delete(p);
	}
	
	public void deleteById(Long id) {
		propertyRepository.deleteById(id);
	}

	public Property findByCity(String city) {
		return propertyRepository.findByCity(city);
	}
	
	public List<Property> findByAgent(Agent agent) {
	    return this.propertyRepository.findByAgent(agent);
	}
	
	public List<Property> findAllByCity(String city) {
	    return this.propertyRepository.findAllByCity(city);
	}
}
