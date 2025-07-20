package it.uniroma3.siw.controller;

import java.util.List;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
public class ContractController {

    @Autowired private ContractService contractService;
    @Autowired private PropertyService propertyService;
    @Autowired private ClientService clientService;
    @Autowired private AgentService agentService;
    @Autowired private CredentialsService credentialsService;

    // --- ADMIN ROUTES ---

    @GetMapping("/admin/manageContracts")
    public String listAllContractsAdmin(Model model) {
        List<Contract> contracts = this.contractService.findAll();
        model.addAttribute("contracts", contracts);
        return "admin/manageContracts.html";
    }

    @GetMapping("/admin/contract/{id}")
    public String getContractAdmin(@PathVariable Long id, Model model) {
        model.addAttribute("contract", this.contractService.findById(id));
        return "admin/contract.html";
    }

    @GetMapping("/admin/formNewContract")
    public String formNewContractAdmin(Model model) {
        model.addAttribute("contract", new Contract());
        model.addAttribute("properties", this.propertyService.findAll());
        model.addAttribute("clients", this.clientService.findAll());
        model.addAttribute("agents", this.agentService.findAll());
        return "admin/formNewContract.html";
    }

    @PostMapping("/admin/saveContract")
    public String saveContractAdmin(@Valid @ModelAttribute Contract contract,
                                    BindingResult bindingResult,
                                    @RequestParam Long propertyId,
                                    @RequestParam Long clientId,
                                    @RequestParam Long agentId,
                                    Model model) {
        if (!bindingResult.hasErrors()) {
            Property p = this.propertyService.findById(propertyId);
            Client c = this.clientService.findById(clientId);
            Agent a = this.agentService.findById(agentId);

            contract.setProperty(p);
            contract.setClient(c);
            contract.setAgent(a);
            this.contractService.save(contract);
            return "redirect:/admin/contract/" + contract.getId();
        }
        // in caso di errori, rimandiamo al form mantenendo le liste
        model.addAttribute("properties", this.propertyService.findAll());
        model.addAttribute("clients", this.clientService.findAll());
        model.addAttribute("agents", this.agentService.findAll());
        return "admin/formNewContract.html";
    }

    @GetMapping("/admin/removeContract/{id}")
    public String removeContractAdmin(@PathVariable Long id) {
        Contract ct = this.contractService.findById(id);
        this.contractService.delete(ct);
        return "redirect:/admin/manageContracts";
    }

    // --- AGENT ROUTES ---

    @GetMapping("/agent/manageContracts")
    public String listContractsAgent(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Agent me = this.credentialsService.getCredentials(username).getUser().getAgent();

        List<Contract> contracts = this.contractService.findByAgent(me);
        model.addAttribute("contracts", contracts);
        return "agent/manageContracts.html";
    }

    @GetMapping("/agent/contract/{id}")
    public String getContractAgent(@PathVariable Long id, Model model) {
        Contract ct = this.contractService.findById(id);
        // verifica che l'agente possa vederlo
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Agent me = this.credentialsService.getCredentials(auth.getName()).getUser().getAgent();
        if (!ct.getAgent().equals(me)) {
            return "error/403.html";
        }
        model.addAttribute("contract", ct);
        return "agent/contract.html";
    }

    @GetMapping("/agent/formNewContract")
    public String formNewContractAgent(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Agent me = this.credentialsService.getCredentials(auth.getName()).getUser().getAgent();

        model.addAttribute("contract", new Contract());
        model.addAttribute("properties", this.propertyService.findByAgent(me));
        model.addAttribute("clients", this.clientService.findAll());
        return "agent/formNewContract.html";
    }

    @PostMapping("/agent/saveContract")
    public String saveContractAgent(@Valid @ModelAttribute Contract contract,
                                    BindingResult bindingResult,
                                    @RequestParam Long propertyId,
                                    @RequestParam Long clientId,
                                    Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Agent me = this.credentialsService.getCredentials(auth.getName()).getUser().getAgent();

        if (!bindingResult.hasErrors()) {
            Property p = this.propertyService.findById(propertyId);
            Client c = this.clientService.findById(clientId);
            // imposto l'agente corrente
            contract.setProperty(p);
            contract.setClient(c);
            contract.setAgent(me);
            this.contractService.save(contract);
            return "redirect:/agent/manageContracts";
        }
        // in caso di errori
        model.addAttribute("properties", this.propertyService.findByAgent(me));
        model.addAttribute("clients", this.clientService.findAll());
        return "agent/formNewContract.html";
    }

    @GetMapping("/agent/removeContract/{id}")
    public String removeContractAgent(@PathVariable Long id) {
        Contract ct = this.contractService.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Agent me = this.credentialsService.getCredentials(auth.getName()).getUser().getAgent();
        if (ct.getAgent().equals(me)) {
            this.contractService.delete(ct);
            return "redirect:/agent/manageContracts";
        }
        return "error/403.html";
    }
    
    @GetMapping("/admin/formUpdateContract/{id}")
    public String formEditContractAdmin(@PathVariable Long id, Model model) {
        Contract ct = contractService.findById(id);

        model.addAttribute("contract", ct);
        model.addAttribute("properties", propertyService.findAll());
        model.addAttribute("clients",    clientService.findAll());
        model.addAttribute("agents",     agentService.findAll());
        
        model.addAttribute("isAdmin", true);
        return "admin/formUpdateContract.html";
    }

    // salvataggio
    @PostMapping("/admin/formUpdateContract/{id}")
    public String formUpdateContractAdmin(@PathVariable Long id,
                                    @Valid @ModelAttribute Contract contract,
                                    BindingResult bindingResult,
                                    Model model) {

        if (bindingResult.hasErrors()) {             // torni al form con liste
            model.addAttribute("properties", propertyService.findAll());
            model.addAttribute("clients",    clientService.findAll());
            model.addAttribute("agents",     agentService.findAll());
            model.addAttribute("isAdmin", true);
            return "admin/formUpdateContract.html";
        }

        contractService.save(contract);
        model.addAttribute("isAdmin", true);
        return "redirect:/admin/manageContracts";
    }

    /* ----------  AGENTE  ---------- */

    @GetMapping("/agent/formUpdateContract/{id}")
    public String formEditContractAgent(@PathVariable Long id, Model model) {
        Contract ct = contractService.findById(id);
        Agent me   = credentialsService
                        .getCredentials(SecurityContextHolder.getContext().getAuthentication().getName())
                        .getUser().getAgent();

        if (!ct.getAgent().equals(me))               // blocco accessi indebiti
            return "error/403.html";

        model.addAttribute("contract", ct);
        model.addAttribute("properties", propertyService.findByAgent(me));
        model.addAttribute("clients",    clientService.findAll());
        model.addAttribute("isAdmin", false);
        return "agent/formUpdateContract.html";
    }

    @PostMapping("/agent/formUpdateContract/{id}")
    public String formUpdateContract(@PathVariable Long id,
                                    @Valid @ModelAttribute Contract contract,
                                    BindingResult bindingResult,
                                    Model model) {

        Agent me = credentialsService
                     .getCredentials(SecurityContextHolder.getContext().getAuthentication().getName())
                     .getUser().getAgent();

        if (bindingResult.hasErrors()) {
            model.addAttribute("properties", propertyService.findByAgent(me));
            model.addAttribute("clients",    clientService.findAll());
            model.addAttribute("isAdmin", false);
            return "agent/formUpdateContract.html";
        }
        contract.setAgent(me);
        
        contractService.save(contract);
        model.addAttribute("isAdmin", false);
        return "redirect:/agent/manageContracts";
    }
}
