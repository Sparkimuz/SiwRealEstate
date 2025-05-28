package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.RealEstateAgency;
import it.uniroma3.siw.repository.RealEstateAgencyRepository;

@Service
public class RealEstateAgencyService {

    @Autowired
    private RealEstateAgencyRepository realEstateAgencyRepository;

    public RealEstateAgency findByCity(String city) {
        return realEstateAgencyRepository.findByCity(city);
    }
    
    public List<RealEstateAgency> findAll() {
        return (List<RealEstateAgency>) this.realEstateAgencyRepository.findAll();
    }

    public RealEstateAgency findById(Long id) {
        Optional<RealEstateAgency> result = this.realEstateAgencyRepository.findById(id);
        return result.orElse(null);
    }

    public void delete(RealEstateAgency r) {
        this.realEstateAgencyRepository.delete(r);
    }

    public void save(RealEstateAgency agency) {
        this.realEstateAgencyRepository.save(agency);
    }
}