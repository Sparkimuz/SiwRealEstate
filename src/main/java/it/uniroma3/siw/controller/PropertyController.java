package it.uniroma3.siw.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.model.Contract;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Property;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.PropertyRepository;
import it.uniroma3.siw.service.AgentService;
import it.uniroma3.siw.service.ContractService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.PropertyService;
import it.uniroma3.siw.validator.PropertyValidator;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;

@Controller
public class PropertyController {

    //private static final String UPLOAD_DIR = "C:\\Users\\andre\\Documents\\workspace-spring-tools-for-eclipse-4.30.0.RELEASE\\SiwRealEstate\\src\\main\\resources\\static\\images";		
	private static final String UPLOAD_DIR= "C:\\\\Users\\\\tcenc\\\\Documents\\\\workspace-spring-tools-for-eclipse-4.30.0.RELEASE\\\\SiwRealEstate\\\\src\\\\main\\\\resources\\\\static\\\\images";


	@Autowired
	PropertyService propertyService;

	@Autowired
	AgentService agentService;

	@Autowired
	PropertyRepository propertyRepository;

	@Autowired
	ContractService contractService;

	@Autowired
	GlobalController gc;

	@Autowired
	CredentialsService credentialsService;

	@Autowired
	PropertyValidator propertyValidator;

	@Autowired
	EntityManager entityManager;


	@GetMapping(value = "/property/{id}")
	public String getProperty(@PathVariable("id") Long id, Model model) {
		Property p = this.propertyService.findById(id);
		UserDetails u = gc.getUser();
		if(u!=null) {
			String username = u.getUsername();
			Credentials credenziali = this.credentialsService.getCredentials(username);
			User user = credenziali.getUser();
			model.addAttribute("user", user);
			Agent agent=user.getAgent();
			boolean sold=false;
			for(Contract c: agent.contracts) {
				if(c.property.getId()==c.getId()) {
					sold=true;
					break;
				}
			}
			model.addAttribute("sold", sold);
		}

		model.addAttribute("property", p);
		return "property.html";
	}

	@GetMapping("/admin/manageProperties")
    public String adminManageProperties(Model model) {
        model.addAttribute("properties", propertyService.findAll());
        return "admin/manageProperties.html";     // view già presente nei template
    }
	
	@GetMapping(value = "/properties")
	public String showProperties(Model model) {
		model.addAttribute("properties", this.propertyService.findAllAvailable());
		return "properties.html";
	}
	
	/* --- LISTA IMMOBILI PER CITTÀ (pubblico) --- */
	
	@GetMapping(value = "/formSearchProperty")
	public String formSearchProperty(String city, Model model) {
		List<Property> properties = propertyService.findAllAvailableByCity(city);
	    model.addAttribute("properties", properties);
	    model.addAttribute("city", city);
		return "formSearchProperty.html";
	}

	@GetMapping(value = "/agent/addProperty")
	public String formNewProperty(Model model) {
		Property property = new Property();
		model.addAttribute("property", property);
		return "agent/addProperty.html";
	}

	@GetMapping("/deleteProperty/{id}")
	public String deleteProperty(@PathVariable("id") Long id, Model model) {
		this.propertyService.deleteById(id);
		model.addAttribute("properties", this.propertyService.findAllAvailable());
		return "properties.html";
	}
	
	/* ========== FORM UPDATE ========== */
	@GetMapping("/agent/formUpdateProperty/{id}")
	public String formUpdateProperty(@PathVariable Long id, Model model) {
	    Property property = propertyService.findById(id);
	    model.addAttribute("property", property);
	    return "agent/formUpdateProperty.html";
	}

	/* ========== SUBMIT UPDATE ========== */
	@PostMapping("/agent/updateProperty")
	public String updateProperty(@Valid @ModelAttribute("property") Property property,
	                             BindingResult bindingResult,
	                             @RequestParam("immagine") MultipartFile file,
	                             Model model) {

	    // validazione custom
	    propertyValidator.validate(property, bindingResult);

	    if (bindingResult.hasErrors()) {
	        return "agent/formUpdateProperty.html";
	    }

	    // se arriva un file, sovrascrive l’immagine
	    if (!file.isEmpty()) {
	        try {
	            String fileName = StringUtils.cleanPath(file.getOriginalFilename())
	                                          .replaceAll("\\s+", "_");
	            Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
	            Files.createDirectories(path.getParent());
	            Files.write(path, file.getBytes());
	            property.setUrlImage(fileName);
	        } catch (IOException e) {
	            e.printStackTrace();
	            model.addAttribute("fileError", "Errore nel caricamento dell’immagine");
	            return "agent/formUpdateProperty.html";
	        }
	    } else {
	        // preserva la vecchia immagine
	        Property original = propertyService.findById(property.getId());
	        property.setUrlImage(original.getUrlImage());
	    }

	    propertyService.save(property);
	    return "redirect:/agent/manageProperties";
	}

	@PostMapping(value = "/property")
	public String newProperty(@Valid @ModelAttribute("property") Property property,
			@RequestParam("immagine") MultipartFile file,
			BindingResult propertyBindingResult, Model model) {
		UserDetails u = gc.getUser();
		String username = u.getUsername();
		Credentials credentials = this.credentialsService.getCredentials(username);
		User currentUser = credentials.getUser();
		Agent agent = currentUser.getAgent();

		property.setAgent(agent);  // collega agente loggato
		property.setContracts(new ArrayList<>());  // se usi esplicitamente i contratti

		this.propertyValidator.validate(property, propertyBindingResult);
		if (!propertyBindingResult.hasErrors()) {
			if (!file.isEmpty()) {
				try {
					String fileName = StringUtils.cleanPath(file.getOriginalFilename());
					Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
					Files.write(path, file.getBytes());
					property.setUrlImage(fileName);

					agent.getProperties().add(property);  // aggiorna proprietà lato agente
					this.propertyService.save(property);

					model.addAttribute("property", property);
					return "redirect:/property/" + property.getId();

				} catch (IOException e) {
					e.printStackTrace();
					return "agent/addProperty.html";
				}
			}
		}

		if (credentials.getRole().equals("AGENT"))
			return "/agent/addProperty.html";
		else
			return "/admin/addProperty.html";
	}

	@GetMapping(value = "/admin/addProperty")
	public String AdminFormNewProperty(Model model) {
		Property property = new Property();
		model.addAttribute("property", property);
		return "admin/addProperty.html";
	}

	@GetMapping("admin/removeProperty/{id}")
	public String AdminRemoveProperty(@PathVariable("id") Long id, Model model) {
		this.propertyService.deleteById(id);
		model.addAttribute("properties", this.propertyService.findAll());
		return "redirect:/admin/manageProperties";
	}

	@GetMapping("/admin/addPropertyToAgent/{idAgent}")
	public String addPropertyToAgent(@PathVariable("idAgent") Long idAgent, Model model) {
		Agent agent = this.agentService.findById(idAgent);
		model.addAttribute("agent", agent);

		Property property = new Property();
		model.addAttribute("property", property);

		return "admin/addPropertyToAgent.html";
	}

	@PostMapping("/admin/addPropertyToAgent")
	public String setPropertyToAgent(@Valid @ModelAttribute("property") Property property,
	                                 BindingResult bindingResult,
	                                 @RequestParam("agentId") Long agentId,
	                                 @RequestParam("immagine") MultipartFile file) {
	    Agent agent = this.agentService.findById(agentId);
	    property.setAgent(agent);
	    property.setContracts(new ArrayList<>());

	    this.propertyValidator.validate(property, bindingResult);
	    if (!bindingResult.hasErrors()) {
	        if (!file.isEmpty()) {
	            try {
	                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
	                Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
	                Files.write(path, file.getBytes());

	                property.setUrlImage(fileName);
	                agent.getProperties().add(property);
	                this.propertyService.save(property);

	                return "redirect:/property/" + property.getId();

	            } catch (IOException e) {
	                e.printStackTrace();
	                return "redirect:/admin/addPropertyToAgent/" + agent.getId();
	            }
	        }
	    }

	    return "redirect:/admin/addPropertyToAgent/" + agent.getId();
	}



}
