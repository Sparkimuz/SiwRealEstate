package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import it.uniroma3.siw.model.RealEstateAgency;

public interface RealEstateAgencyRepository {
	
	public RealEstateAgency findByCity(String city);
	
	@Query("SELECT r FROM RealEstateAgency r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
	public List<RealEstateAgency> findRealEstateAgencies(String city);

}
