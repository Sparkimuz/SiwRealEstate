package it.uniroma3.siw.service;

import java.util.List;

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

    public List<RealEstateAgency> searchByName(String name) {
        return realEstateAgencyRepository.findRealEstateAgencies(name);
    }
}
