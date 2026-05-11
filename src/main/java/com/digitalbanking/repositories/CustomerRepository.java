package com.digitalbanking.repositories;

import com.digitalbanking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité Customer.
 * Hérite automatiquement de toutes les opérations CRUD de JpaRepository.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Recherche des clients dont le nom contient le mot-clé (insensible à la casse).
     * Génère la requête JPQL : SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(%keyword%)
     *
     * @param keyword mot-clé de recherche
     * @return liste des clients correspondants
     */
    List<Customer> findByNameContainsIgnoreCase(String keyword);
}
