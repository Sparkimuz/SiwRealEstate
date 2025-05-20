package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Client;
import it.uniroma3.siw.model.Property;
import it.uniroma3.siw.repository.ClientRepository;



@Service
public class ClientService {

	@Autowired 
	private ClientRepository clientRepository;
	
	public Client findById( Long id) {
		return clientRepository.findById(id).get();
	}
	
	public Iterable<Client> findAll(){
		return clientRepository.findAll();
	}
	
	public void save(Client c) {
		clientRepository.save(c);
	}
	

	public List<Client> findBySurname(String surname) {
		return clientRepository.findBySurname(surname);
	}
	
	public void remove(Client c) {
		clientRepository.delete(c);
	}

	public boolean existsByNameSurnameBirth(Client c) {
		return clientRepository.existsByName(c.getName()) && clientRepository.existsBySurname(c.getSurname()) && 
				clientRepository.existsByBirthdate(c.getBirthdate());
	}
}
