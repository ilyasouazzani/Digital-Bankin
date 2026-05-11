package com.digitalbanking.repositories;

import com.digitalbanking.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité BankAccount (et ses sous-classes).
 * JPA utilise le polymorphisme pour récupérer CurrentAccount et SavingAccount.
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    /**
     * Récupère tous les comptes d'un client donné par son ID.
     *
     * @param customerId identifiant du client
     * @return liste des comptes bancaires du client
     */
    List<BankAccount> findByCustomerId(Long customerId);
}
