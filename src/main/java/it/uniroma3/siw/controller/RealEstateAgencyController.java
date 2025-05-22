package it.uniroma3.siw.controller;

import java.util.List;

import it.uniroma3.siw.model.RealEstateAgency;
import it.uniroma3.siw.service.RealEstateAgencyService;
import it.uniroma3.siw.validator.RealEstateAgencyValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.service.RealEstateAgencyService;


@Controller
public class RealEstateAgencyController {

    @Autowired
    private RealEstateAgencyService agencyService;
    @Autowired
    private RealEstateAgencyValidator agencyValidator;

    // --- ROTTE PUBBLICHE (tutti, anche non loggati) ---

    @GetMapping("/realestateagencies")
    public String showAgencies(Model model) {
        List<RealEstateAgency> agencies = this.agencyService.findAll();
        model.addAttribute("agencies", agencies);
        return "realestateagencies.html";
    }

    @GetMapping("/realEstateAgency/{id}")
    public String showAgency(@PathVariable("id") Long id, Model model) {
        RealEstateAgency agency = this.agencyService.findById(id);
        model.addAttribute("agency", agency);
        // la view potrà iterare agency.getAgents() per mostrare gli agenti
        return "realEstateAgency.html";
    }

    // --- ROTTE ADMIN (solo ADMIN) ---

    @GetMapping("/admin/agencies")
    public String manageAgencies(Model model) {
        List<RealEstateAgency> agencies = this.agencyService.findAll();
        model.addAttribute("agencies", agencies);
        return "admin/manageAgencies.html";
    }

    @GetMapping("/admin/formNewAgency")
    public String formNewAgency(Model model) {
        model.addAttribute("agency", new RealEstateAgency());
        return "admin/formNewAgency.html";
    }

    @PostMapping("/admin/saveAgency")
    public String saveAgency(@Valid @ModelAttribute("agency") RealEstateAgency agency,
                             BindingResult bindingResult,
                             Model model) {
        this.agencyValidator.validate(agency, bindingResult);
        if (!bindingResult.hasErrors()) {
            this.agencyService.save(agency);
            return "redirect:/admin/agencies";
        }
        return "admin/formNewAgency.html";
    }

    @GetMapping("/admin/formUpdateAgency/{id}")
    public String formUpdateAgency(@PathVariable("id") Long id, Model model) {
        RealEstateAgency agency = this.agencyService.findById(id);
        model.addAttribute("agency", agency);
        return "admin/formUpdateAgency.html";
    }

    @PostMapping("/admin/updateAgency")
    public String updateAgency(@Valid @ModelAttribute("agency") RealEstateAgency agency,
                               BindingResult bindingResult,
                               Model model) {
        this.agencyValidator.validate(agency, bindingResult);
        if (!bindingResult.hasErrors()) {
            this.agencyService.save(agency);
            return "redirect:/admin/agencies";
        }
        return "admin/formUpdateAgency.html";
    }

    @GetMapping("/admin/removeAgency/{id}")
    public String removeAgency(@PathVariable("id") Long id) {
        RealEstateAgency agency = this.agencyService.findById(id);
        this.agencyService.delete(agency);
        return "redirect:/admin/agencies";
    }

    /*@GetMapping("/realestateagencies")
    public String listAgencies(Model model) {
        model.addAttribute("agencies", agencyService.findAll());
        return "realestateagencies.html";   // già presente in templates
    }*/
}
