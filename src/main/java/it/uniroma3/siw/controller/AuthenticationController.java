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

    private static final String UPLOAD_DIR =
        "C:\\Users\\tcenc\\Documents\\workspace-spring-tools-for-eclipse-4.30.0.RELEASE\\SiwRealEstate\\src\\main\\resources\\static\\images";

    @Autowired private CredentialsService credentialsService;
    @Autowired private UserService        userService;
    @Autowired private AgentService       agentService;

    @Autowired private CredentialsValidator credentialsValidator;
    @Autowired private UserValidator        userValidator;

    /* ---------- PAGINE PUBBLICHE ---------- */

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "register.html";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login.html";
    }

    /* ---------- HOME smart ---------- */

    @GetMapping({"/", "/index"})
    public String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken)     // non loggato
            return "index.html";

        return "redirect:/success";                           // delega
    }

    /* ---------- DOPO LOGIN ---------- */

    @GetMapping("/success")
    public String defaultAfterLogin() {

        UserDetails userDetails =
            (UserDetails) SecurityContextHolder.getContext()
                                               .getAuthentication()
                                               .getPrincipal();
        Credentials creds = credentialsService.getCredentials(userDetails.getUsername());

        if (Credentials.ADMIN_ROLE.equals(creds.getRole()))
            return "admin/indexAdmin.html";

        return "agent/indexAgent.html";
    }

    /* ---------- REGISTRA NUOVO UTENTE (→ AGENT) ---------- */

    @PostMapping("/register")
    public String registerUser(
            @Valid   @ModelAttribute("user")        User        user,
                     BindingResult                  userErrors,
            @Valid   @ModelAttribute("credentials") Credentials credentials,
            @RequestParam("immagine") MultipartFile file,
                     BindingResult                  credErrors,
                     Model                          model) {

        credentialsValidator.validate(credentials, credErrors);
        userValidator.validate(user, userErrors);

        if (credErrors.hasErrors() || userErrors.hasErrors())
            return "register.html";

        /* ---- salva eventuale immagine ---- */
        if (!file.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                Path   path     = Paths.get(UPLOAD_DIR + File.separator + fileName);
                Files.write(path, file.getBytes());
                user.setUrlImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return "register.html";
            }
        }

        /* ---- persistenza ---- */
        userService.saveUser(user);
        credentials.setUser(user);             // FK
        credentialsService.saveCredentials(credentials);   // imposta ruolo AGENT

        /*  creiamo subito l’entità Agent collegata all’utente  */
        Agent agent = new Agent();
        agent.setName(user.getName());
        agent.setSurname(user.getSurname());
        agent.setBirthdate(user.getBirthdate());
        agent.setUrlImage(user.getUrlImage());
        user.setAgent(agent);
        agentService.save(agent);

        model.addAttribute("user", user);
        return "login.html";
    }
}
