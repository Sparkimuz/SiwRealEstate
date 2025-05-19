package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.RealEstateAgency;

public interface RealEstateAgencyRepository extends CrudRepository<RealEstateAgency, Long>{
	
	public RealEstateAgency findByCity(String city);
	

}
