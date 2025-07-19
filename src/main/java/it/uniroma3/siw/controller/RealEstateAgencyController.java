package it.uniroma3.siw.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Agent;
import it.uniroma3.siw.model.RealEstateAgency;
import it.uniroma3.siw.service.AgentService;
import it.uniroma3.siw.service.RealEstateAgencyService;
import it.uniroma3.siw.validator.RealEstateAgencyValidator;

@Controller
@RequestMapping
public class RealEstateAgencyController {

    private static final String UPLOAD_DIR = "C:\\Users\\andre\\Documents\\workspace-spring-tools-for-eclipse-4.30.0.RELEASE\\SiwRealEstate\\src\\main\\resources\\static\\images";

    @Autowired
    private RealEstateAgencyService agencyService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private RealEstateAgencyValidator agencyValidator;

    /* ======================= PUBLIC (no authentication) ==================== */

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
        return "realEstateAgency.html";
    }

    /* =============================== ADMIN ONLY ============================ */

    @GetMapping("/admin/manageAgencies")
    public String manageAgencies(Model model) {
        model.addAttribute("agencies", this.agencyService.findAll());
        return "admin/manageAgencies.html";
    }

    @GetMapping("/admin/formNewAgency")
    public String showFormNewAgency(Model model) {
        model.addAttribute("agency", new RealEstateAgency());
        return "admin/formNewAgency.html";
    }

    @PostMapping("/admin/saveRealEstateAgency")
    public String saveAgency(@Valid @ModelAttribute("agency") RealEstateAgency agency,
                             BindingResult bindingResult,
                             @RequestParam("immagine") MultipartFile file,
                             Model model) {
        agencyValidator.validate(agency, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/formNewAgency.html";
        }

        if (!file.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename()).replaceAll("\\s+", "_");
                Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
                agency.setUrlImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("fileError", "Errore nel caricamento dell’immagine");
                return "admin/formNewAgency.html";
            }
        }

        agencyService.save(agency);
        return "redirect:/admin/manageAgencies";
    }

    @GetMapping("/admin/formUpdateAgency/{id}")
    public String formUpdateAgency(@PathVariable("id") Long id, Model model) {
        model.addAttribute("agency", agencyService.findById(id));
        return "admin/formUpdateAgency.html";
    }

    @PostMapping("/admin/formUpdateRealEstateAgency")
    public String formUpdateAgency(@Valid @ModelAttribute("agency") RealEstateAgency agency,
                               BindingResult bindingResult,
                               @RequestParam("immagine") MultipartFile file,
                               Model model) {
        agencyValidator.validate(agency, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/formUpdateAgency.html";
        }

        if (!file.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename()).replaceAll("\\s+", "_");
                Path path = Paths.get(UPLOAD_DIR + File.separator + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
                agency.setUrlImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("fileError", "Errore nel caricamento dell’immagine");
                return "admin/formUpdateAgency.html";
            }
        } else {
            RealEstateAgency original = agencyService.findById(agency.getId());
            agency.setUrlImage(original.getUrlImage());
        }

        agencyService.save(agency);
        return "redirect:/admin/manageAgencies";
    }

    @GetMapping("/admin/deleteRealEstateAgency/{id}")
    public String removeAgency(@PathVariable("id") Long id) {
        RealEstateAgency agency = agencyService.findById(id);

        // Scollega gli agenti dall'agenzia prima di eliminare l'agenzia
        if (agency.getAgents() != null) {
            for (Agent agent : agency.getAgents()) {
                agent.setRealEstateAgency(null);
                agentService.save(agent);
            }
        }

        agencyService.delete(agency);
        return "redirect:/admin/manageAgencies";
    }
}
