package it.uniroma3.siw.controller;

import java.util.List;

import it.uniroma3.siw.model.Client;
import it.uniroma3.siw.repository.ClientRepository;
import it.uniroma3.siw.service.ClientService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.validator.ClientValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientValidator clientValidator;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/client/{id}")
    public String getClient(@PathVariable("id") Long id, Model model) {
        model.addAttribute("client", this.clientService.findById(id));
        return "client.html";
    }

    @GetMapping("/clients")
    public String showClients(Model model) {
        model.addAttribute("clients", this.clientService.findAll());
        return "clients.html";
    }

    @PostMapping("/formSearchClient")
    public String searchClient(@RequestParam String name, Model model) {
        List<Client> clients = this.clientRepository.findByNameContainingIgnoreCase(name);
        model.addAttribute("clients", clients);
        return "clients.html";
    }
    
    @GetMapping("/agent/formNewClient")
    public String formNewClientForAgent(Model model) {
        model.addAttribute("client", new Client());
        return "agent/formNewClient.html";
    }

    @PostMapping("/agent/saveClient")
    public String saveClientByAgent(@Valid @ModelAttribute("client") Client client,
                                    BindingResult clientBindingResult, Model model) {
        this.clientValidator.validate(client, clientBindingResult);
        if (!clientBindingResult.hasErrors()) {
            this.clientService.save(client);
            model.addAttribute("client", client);
            return "redirect:/agent/formNewContract";
        }
        return "agent/formNewClient.html";
    }
    
    @GetMapping("/agent/manageClients")
    public String manageClientsByAgent(Model model) {
    	model.addAttribute("clients", this.clientService.findAll());
        return "agent/formNewContrct.html";
    }

    @GetMapping("/agent/removeClient/{id}")
    public String removeClientByAgent(@PathVariable("id") Long id) {
        Client c = this.clientService.findById(id);
        this.clientService.remove(c);
        return "redirect:/agent/manageClients";
    }


    @GetMapping("/admin/formNewClient")
    public String formNewClient(Model model) {
        model.addAttribute("client", new Client());
        return "admin/formNewClient.html";
    }

    @PostMapping("/admin/saveClient")
    public String newClient(@Valid @ModelAttribute("client") Client client,
                            BindingResult clientBindingResult,
                            Model model) {
        this.clientValidator.validate(client, clientBindingResult);
        if (!clientBindingResult.hasErrors()) {
            this.clientService.save(client);
            model.addAttribute("client", client);
            return "redirect:/client/" + client.getId();
        }
        return "admin/formNewClient.html";
    }

    @GetMapping("/admin/manageClients")
    public String manageClients(Model model) {
    	model.addAttribute("clients", this.clientService.findAll());
        return "admin/manageClients.html";
    }

    @GetMapping("/admin/removeClient/{id}")
    public String removeClient(@PathVariable("id") Long id) {
        Client c = this.clientService.findById(id);
        this.clientService.remove(c);
        return "redirect:/admin/manageClients";
    }
}
