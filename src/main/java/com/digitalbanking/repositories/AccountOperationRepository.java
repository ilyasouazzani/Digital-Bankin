package com.digitalbanking.repositories;

import com.digitalbanking.entities.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité AccountOperation.
 */
@Repository
public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {

    /**
     * Récupère toutes les opérations d'un compte bancaire, sans pagination.
     *
     * @param bankAccountId identifiant du compte
     * @return liste de toutes les opérations
     */
    List<AccountOperation> findByBankAccountId(String bankAccountId);

    /**
     * Récupère les opérations d'un compte bancaire avec pagination.
     * Spring Data génère automatiquement la requête SQL avec LIMIT/OFFSET.
     *
     * @param accountId identifiant du compte
     * @param pageable  informations de pagination (page, taille, tri)
     * @return page d'opérations
     */
    Page<AccountOperation> findByBankAccountId(String accountId, Pageable pageable);
}
