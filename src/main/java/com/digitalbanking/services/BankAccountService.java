package com.digitalbanking.services;

import com.digitalbanking.dtos.*;
import com.digitalbanking.exceptions.BalanceNotSufficientException;
import com.digitalbanking.exceptions.BankAccountNotFoundException;
import com.digitalbanking.exceptions.CustomerNotFoundException;

import java.util.List;

/**
 * Interface définissant le contrat du service bancaire.
 *
 * Séparation interface / implémentation pour :
 *  - faciliter les tests unitaires (mocking)
 *  - permettre de changer l'implémentation sans impacter les consommateurs
 *  - respecter le principe SOLID d'inversion de dépendances
 */
public interface BankAccountService {

    // ====================================================================
    // Gestion des clients
    // ====================================================================

    /**
     * Enregistre un nouveau client.
     * @param customerDTO données du client à créer
     * @return le client créé avec son ID généré
     */
    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    /**
     * Récupère la liste de tous les clients.
     * @return liste des DTOs clients
     */
    List<CustomerDTO> listCustomers();

    /**
     * Recherche des clients par mot-clé sur le nom.
     * @param keyword mot-clé (insensible à la casse, partiel)
     * @return liste des clients correspondants
     */
    List<CustomerDTO> searchCustomers(String keyword);

    /**
     * Récupère un client par son identifiant.
     * @param id identifiant du client
     * @return le DTO du client trouvé
     * @throws CustomerNotFoundException si le client n'existe pas
     */
    CustomerDTO getCustomer(Long id) throws CustomerNotFoundException;

    /**
     * Met à jour les informations d'un client.
     * @param id identifiant du client à modifier
     * @param customerDTO nouvelles données
     * @return le client mis à jour
     * @throws CustomerNotFoundException si le client n'existe pas
     */
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws CustomerNotFoundException;

    /**
     * Supprime un client par son identifiant.
     * @param id identifiant du client
     * @throws CustomerNotFoundException si le client n'existe pas
     */
    void deleteCustomer(Long id) throws CustomerNotFoundException;

    // ====================================================================
    // Gestion des comptes bancaires
    // ====================================================================

    /**
     * Crée un compte courant pour un client.
     * @param initialBalance solde initial
     * @param overDraft découvert autorisé
     * @param customerId identifiant du propriétaire
     * @return le DTO du compte courant créé
     * @throws CustomerNotFoundException si le client n'existe pas
     */
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException;

    /**
     * Crée un compte épargne pour un client.
     * @param initialBalance solde initial
     * @param interestRate taux d'intérêt annuel
     * @param customerId identifiant du propriétaire
     * @return le DTO du compte épargne créé
     * @throws CustomerNotFoundException si le client n'existe pas
     */
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException;

    /**
     * Récupère la liste de tous les comptes bancaires.
     * @return liste des DTOs (CurrentBankAccountDTO ou SavingBankAccountDTO)
     */
    List<BankAccountDTO> bankAccountList();

    /**
     * Récupère un compte bancaire par son identifiant UUID.
     * @param accountId identifiant du compte
     * @return le DTO correspondant
     * @throws BankAccountNotFoundException si le compte n'existe pas
     */
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;

    /**
     * Récupère tous les comptes d'un client donné.
     * @param customerId identifiant du client
     * @return liste de ses comptes
     */
    List<BankAccountDTO> getCustomerAccounts(Long customerId);

    // ====================================================================
    // Opérations bancaires
    // ====================================================================

    /**
     * Effectue un débit sur un compte.
     * @param accountId identifiant du compte
     * @param amount montant à débiter (doit être positif)
     * @param description libellé de l'opération
     * @throws BankAccountNotFoundException si le compte n'existe pas
     * @throws BalanceNotSufficientException si le solde (+ découvert) est insuffisant
     */
    void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException;

    /**
     * Effectue un crédit sur un compte.
     * @param accountId identifiant du compte
     * @param amount montant à créditer (doit être positif)
     * @param description libellé de l'opération
     * @throws BankAccountNotFoundException si le compte n'existe pas
     */
    void credit(String accountId, double amount, String description)
            throws BankAccountNotFoundException;

    /**
     * Effectue un virement entre deux comptes.
     * Implémenté comme un débit du compte source suivi d'un crédit du compte destination.
     * L'opération est atomique (transactionnelle).
     *
     * @param accountIdSource identifiant du compte débiteur
     * @param accountIdDestination identifiant du compte bénéficiaire
     * @param amount montant du virement
     * @throws BankAccountNotFoundException si l'un des comptes n'existe pas
     * @throws BalanceNotSufficientException si le solde source est insuffisant
     */
    void transfer(String accountIdSource, String accountIdDestination, double amount)
            throws BankAccountNotFoundException, BalanceNotSufficientException;

    // ====================================================================
    // Historique paginé
    // ====================================================================

    /**
     * Récupère l'historique paginé des opérations d'un compte.
     * @param accountId identifiant du compte
     * @param page numéro de page (0-indexed)
     * @param size nombre d'éléments par page
     * @return AccountHistoryDTO contenant la page d'opérations et les métadonnées
     * @throws BankAccountNotFoundException si le compte n'existe pas
     */
    AccountHistoryDTO getAccountHistory(String accountId, int page, int size)
            throws BankAccountNotFoundException;

    /**
     * Récupère toutes les opérations d'un compte (sans pagination).
     * @param accountId identifiant du compte
     * @return liste complète des opérations
     */
    List<AccountOperationDTO> accountHistory(String accountId);
}
