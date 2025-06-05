package it.uniroma3.siw.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Property;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.AgentRepository;
import it.uniroma3.siw.service.AgentService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.validator.AgentValidator;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

@Controller
public class AgentController {
	
	 /* private static final String UPLOAD_DIR = "C:\\Users\\Gabriele\\git\\Concessionario\\SiwConcessionario\\src\\main\\resources\\static\\images"; */
		
	 private static final String UPLOAD_DIR= "C:\\\\Users\\\\tcenc\\\\Documents\\\\workspace-spring-tools-for-eclipse-4.30.0.RELEASE\\\\SiwRealEstate\\\\src\\\\main\\\\resources\\\\static\\\\images"; 
	 
	@Autowired
	AgentService agentService;
	
	@Autowired 
	AgentRepository agentRepository;
	
	@Autowired 
	GlobalController gc;
	
	@Autowired 
	CredentialsService credentialsService;

	@Autowired
	AgentValidator agentValidator;
	
	@Autowired
	EntityManager entityManager;

	@GetMapping(value = "/agent/{id}")
	public String getSupplier(@PathVariable("id") Long id, Model model) {
		model.addAttribute("agent", this.agentService.findById(id));
		return "agent.html";
	}

	@GetMapping(value = "/agents")
	public String showAgents(Model model) {
		model.addAttribute("agents", this.agentService.findAll());
		return "agents.html";
	}

	@PostMapping(value = "/formSearchAgent")
	public String getSupplier(@RequestParam String name, Model model) {
		List<Agent> agents = this.agentRepository.findAgents(name);
		model.addAttribute("agents", agents);
		return "suppliers.html";
	}

	@GetMapping(value = "/admin/formNewAgent")
	public String formNewAgent(Model model) {
		Agent agent = new Agent();
		model.addAttribute("agent", agent);
		return "admin/formNewAgent.html";
	}

	@PostMapping(value = "/sup")
	public String newAgent(@Valid @ModelAttribute("agent") Agent agent,
			@RequestParam("immagine") MultipartFile file, BindingResult supplierBindingResult, Model model) {
		this.agentValidator.validate(agent, supplierBindingResult);
		if (!supplierBindingResult.hasErrors()) {
			try {
				String fileName = StringUtils.cleanPath(file.getOriginalFilename());
				Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
				Files.write(path, file.getBytes());
				agent.setUrlImage(fileName);

				this.agentService.save(agent);
				model.addAttribute("agent", agent);

				return "redirect:agent/" + agent.getId();

			} catch (IOException e) {
				e.printStackTrace();
				return "admin/formNewAgent.html.hmtl";
			}
		}
		return "admin/formNewAgent.html";
	}

	@GetMapping(value = "/admin/manageAgents")
	public String manageAgents(Model model) {
		List<Agent> agents=(List<Agent>) this.agentService.findAll();
		UserDetails u = gc.getUser();
		String username = u.getUsername();
		Credentials credenziali = this.credentialsService.getCredentials(username);
		User utenteCorrente = credenziali.getUser();
		Agent agentCorrente = utenteCorrente.getAgent();
		agents.remove(agentCorrente); 
		model.addAttribute("agents", agents);
		return "admin/manageAgents.html";
	}
	
	@GetMapping("/agent/manageProperties")
	public String manageProperties(Model model) {
	    UserDetails u = gc.getUser();
	    String username = u.getUsername();
	    Credentials credenziali = this.credentialsService.getCredentials(username);
	    User currentUser = credenziali.getUser();
	    Agent currentAgent = currentUser.getAgent();

	    model.addAttribute("properties", currentAgent.getProperties());
	    return "agent/manageProperties.html";
	}


	@GetMapping(value = "/admin/removeAgent/{id}")
	public String removeSupplier(@PathVariable("id") Long id) {
		Agent a = this.agentService.findById(id);
		this.agentService.remove(a);
		return "redirect:/admin/manageAgents";
	}

}
