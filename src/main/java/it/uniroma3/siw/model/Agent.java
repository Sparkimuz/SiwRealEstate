package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Agent {
	

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;

		@OneToMany(mappedBy = "agent", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
		public
		List<Contract> contracts;

		@ManyToOne
		private RealEstateAgency realEstateAgency;

		@OneToMany(mappedBy = "agent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
		private List<Property> properties;
		
		private String name;
		
		private String surname;
		
		private LocalDate birthdate;
		
		private String urlImage;
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public List<Contract> getContracts() {
			return contracts;
		}
		public void setContracts(List<Contract> contracts) {
			this.contracts = contracts;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getSurname() {
			return surname;
		}
		public void setSurname(String surname) {
			this.surname = surname;
		}
		public LocalDate getBirthdate() {
			return birthdate;
		}
		public void setBirthdate(LocalDate birthdate) {
			this.birthdate = birthdate;
		}
		public String getUrlImage() {
			return urlImage;
		}
		public void setUrlImage(String urlImage) {
			this.urlImage = urlImage;
		}
		public List<Property> getProperties() {
			return properties;
		}

		public void setProperties(List<Property> properties) {
			this.properties = properties;
		}

}
