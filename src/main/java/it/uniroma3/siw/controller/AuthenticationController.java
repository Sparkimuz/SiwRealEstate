package it.uniroma3.siw.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AgentService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.validator.CredentialsValidator;
import it.uniroma3.siw.validator.UserValidator;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {
	
	/*private static final String UPLOAD_DIR = "C:\\Users\\Gabriele\\git\\Concessionario\\SiwConcessionario\\src\\main\\resources\\static\\images"; */
	
	 private static final String UPLOAD_DIR= "C:\\Users\\39345\\Documents\\Concessionario-Siw\\SiwConcessionario\\src\\main\\resources\\static\\images"; 
	@Autowired
	private CredentialsService credentialsService;

    @Autowired
	private UserService userService;
    
    @Autowired
    private AgentService agentService;
    
    @Autowired 
    private CredentialsValidator credentialsValidator;
    @Autowired
    private UserValidator userValidator;
	
	@GetMapping(value = "/register") 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "register.html";
	}
	
	@GetMapping(value = "/login") 
	public String showLoginForm (Model model) {
		return "login.html";
	}
	
	@GetMapping("/index")
	public String legacyHome() {
	    return "redirect:/";
	}
	
	@GetMapping(value = "/") 
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "index.html";
		}
		else {		
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
				return "admin/index.html";
			}
		}
        return "agent/index.html";
	}
		
    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {
        
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
            return "admin/index.html";
        }
        return "agent/index.html";
    }

	@PostMapping(value = "/register" )
    public String registerUser(@Valid @ModelAttribute("user") User user,
                 BindingResult userBindingResult, @Valid
                 @ModelAttribute("credentials") Credentials credentials,
                 @RequestParam("immagine") MultipartFile file, BindingResult credentialsBindingResult,
                 Model model) {
		
		this.credentialsValidator.validate(credentials, credentialsBindingResult);
		this.userValidator.validate(user, userBindingResult);
		
		// se user e credential hanno entrambi contenuti validi, memorizza User e the Credentials nel DB
        if(!credentialsBindingResult.hasErrors() && !userBindingResult.hasErrors()) {
        	if (!file.isEmpty())
				try {
					String fileName = StringUtils.cleanPath(file.getOriginalFilename());
					Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
					Files.write(path, file.getBytes());
					user.setUrlImage(fileName);
				} catch (IOException e) {
					e.printStackTrace();
					return "registerUser";
				}
        	
        	userService.saveUser(user);
        	credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            
            
            Agent newAgent= new Agent();
            newAgent.setName(user.getName());
            newAgent.setSurname(user.getSurname());
            newAgent.setBirthdate(user.getBirthdate());
            newAgent.setUrlImage(user.getUrlImage());
            user.setAgent(newAgent);
            this.agentService.save(newAgent);
            
            model.addAttribute("user", user);
            return "login.html";
        }
        return "register.html";
    }
}