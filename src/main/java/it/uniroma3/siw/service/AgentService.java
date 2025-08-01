package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.AgentRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class AgentService {
	@Autowired
    private UserRepository userRepository; 
	
	@Autowired 
	private AgentRepository agentRepository;
	
	public Agent findById( Long id) {
		return agentRepository.findById(id).get();
	}
	
	public Iterable<Agent> findAll(){
		return agentRepository.findAll();
	}
	
	public void save(Agent a) {
		agentRepository.save(a);
	}
	

	public List<Agent> findBySurname(String surname) {
		return agentRepository.findBySurname(surname);
	}
	
	public void remove(Agent a) {
		User u = userRepository.findByAgentId(a.getId());
		if (u != null) {
		    u.setAgent(null);
		}
		agentRepository.delete(a);
	}

	public boolean existsByNameSurnameBirth(Agent a) {
		return agentRepository.existsByName(a.getName()) && agentRepository.existsBySurname(a.getSurname()) && 
				agentRepository.existsByBirthdate(a.getBirthdate());
	}
	

	

}
