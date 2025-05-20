package it.uniroma3.siw.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Contract;
import it.uniroma3.siw.model.Agent;

public interface ContractRepository extends CrudRepository<Contract, Long> {
    List<Contract> findByAgent(Agent agent);
    List<Contract> findByClientId(Long clientId);
    List<Contract> findByPropertyId(Long propertyId);
    
    /**
     * Ritorna TRUE se esiste già almeno un contratto per lo stesso immobile
     * con intervallo [startDate, finalDate] che si sovrappone a quello passato.
     *
     * La logica di sovrapposizione è:
     *     (existing.startDate <= :finalDate) AND (existing.finalDate >= :startDate)
     */
    @Query("""
        SELECT COUNT(c) > 0
        FROM Contract c
        WHERE c.property.id = :propertyId
          AND c.startDate <= :finalDate
          AND c.finalDate >= :startDate
    """)
    boolean existsOverlappingContract(@Param("propertyId") Long propertyId,
                                      @Param("startDate")  LocalDate startDate,
                                      @Param("finalDate")  LocalDate finalDate);
}
