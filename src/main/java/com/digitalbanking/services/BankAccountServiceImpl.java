package com.digitalbanking.services;

import com.digitalbanking.dtos.*;
import com.digitalbanking.entities.*;
import com.digitalbanking.exceptions.BalanceNotSufficientException;
import com.digitalbanking.exceptions.BankAccountNotFoundException;
import com.digitalbanking.exceptions.CustomerNotFoundException;
import com.digitalbanking.mappers.BankAccountMapperImpl;
import com.digitalbanking.repositories.AccountOperationRepository;
import com.digitalbanking.repositories.BankAccountRepository;
import com.digitalbanking.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation de la couche service bancaire.
 *
 * Toutes les méthodes de modification de données sont transactionnelles (@Transactional).
 * Le logger Slf4j (via Lombok) permet de tracer chaque opération métier.
 */
@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountMapperImpl dtoMapper;

    // ====================================================================
    // Gestion des clients
    // ====================================================================

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Création d'un nouveau client : {}", customerDTO.getName());
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> listCustomers() {
        log.info("Récupération de la liste des clients");
        return customerRepository.findAll()
                .stream()
                .map(dtoMapper::fromCustomer)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String keyword) {
        log.info("Recherche de clients avec le mot-clé : {}", keyword);
        return customerRepository.findByNameContainsIgnoreCase(keyword)
                .stream()
                .map(dtoMapper::fromCustomer)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(Long id) throws CustomerNotFoundException {
        log.info("Récupération du client avec l'ID : {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Client introuvable avec l'ID : " + id));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws CustomerNotFoundException {
        log.info("Mise à jour du client avec l'ID : {}", id);
        // Vérifie l'existence du client avant la mise à jour
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Client introuvable avec l'ID : " + id));
        existingCustomer.setName(customerDTO.getName());
        existingCustomer.setEmail(customerDTO.getEmail());
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return dtoMapper.fromCustomer(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) throws CustomerNotFoundException {
        log.info("Suppression du client avec l'ID : {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Client introuvable avec l'ID : " + id));
        customerRepository.delete(customer);
    }

    // ====================================================================
    // Gestion des comptes
    // ====================================================================

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Client introuvable avec l'ID : " + customerId));
        log.info("Création d'un compte courant pour le client : {}", customer.getName());

        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setStatus(AccountStatus.CREATED);
        currentAccount.setCurrency("EUR");
        currentAccount.setCustomer(customer);

        CurrentAccount savedCurrentAccount = (CurrentAccount) bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedCurrentAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Client introuvable avec l'ID : " + customerId));
        log.info("Création d'un compte épargne pour le client : {}", customer.getName());

        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setStatus(AccountStatus.CREATED);
        savingAccount.setCurrency("EUR");
        savingAccount.setCustomer(customer);

        SavingAccount savedSavingAccount = (SavingAccount) bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedSavingAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDTO> bankAccountList() {
        log.info("Récupération de la liste de tous les comptes bancaires");
        return bankAccountRepository.findAll()
                .stream()
                .map(bankAccount -> {
                    if (bankAccount instanceof CurrentAccount ca) {
                        return (BankAccountDTO) dtoMapper.fromCurrentBankAccount(ca);
                    } else {
                        return (BankAccountDTO) dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        log.info("Récupération du compte : {}", accountId);
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Compte introuvable avec l'ID : " + accountId));

        if (bankAccount instanceof CurrentAccount ca) {
            return dtoMapper.fromCurrentBankAccount(ca);
        } else {
            return dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDTO> getCustomerAccounts(Long customerId) {
        log.info("Récupération des comptes du client : {}", customerId);
        return bankAccountRepository.findByCustomerId(customerId)
                .stream()
                .map(bankAccount -> {
                    if (bankAccount instanceof CurrentAccount ca) {
                        return (BankAccountDTO) dtoMapper.fromCurrentBankAccount(ca);
                    } else {
                        return (BankAccountDTO) dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);
                    }
                })
                .collect(Collectors.toList());
    }

    // ====================================================================
    // Opérations bancaires
    // ====================================================================

    @Override
    public void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Compte introuvable avec l'ID : " + accountId));

        // Vérification du solde disponible (solde + découvert pour les comptes courants)
        double availableBalance = bankAccount.getBalance();
        if (bankAccount instanceof CurrentAccount ca) {
            availableBalance += ca.getOverDraft();
        }

        if (availableBalance < amount) {
            throw new BalanceNotSufficientException(
                    "Solde insuffisant. Disponible : " + availableBalance + " — Demandé : " + amount);
        }

        log.info("Débit de {} sur le compte {}", amount, accountId);

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        // Audit : récupère l'utilisateur connecté du SecurityContext (Partie 3)
        accountOperation.setPerformedBy(getAuthenticatedUsername());
        accountOperationRepository.save(accountOperation);

        // Mise à jour du solde
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description)
            throws BankAccountNotFoundException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Compte introuvable avec l'ID : " + accountId));

        log.info("Crédit de {} sur le compte {}", amount, accountId);

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setPerformedBy(getAuthenticatedUsername());
        accountOperationRepository.save(accountOperation);

        // Mise à jour du solde
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount)
            throws BankAccountNotFoundException, BalanceNotSufficientException {

        log.info("Virement de {} du compte {} vers le compte {}", amount, accountIdSource, accountIdDestination);
        debit(accountIdSource, amount, "Virement vers " + accountIdDestination);
        credit(accountIdDestination, amount, "Virement reçu de " + accountIdSource);
    }

    // ====================================================================
    // Historique des opérations
    // ====================================================================

    @Override
    @Transactional(readOnly = true)
    public List<AccountOperationDTO> accountHistory(String accountId) {
        log.info("Récupération de l'historique complet du compte : {}", accountId);
        return accountOperationRepository.findByBankAccountId(accountId)
                .stream()
                .map(dtoMapper::fromAccountOperation)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size)
            throws BankAccountNotFoundException {

        log.info("Historique paginé du compte {} — page={}, size={}", accountId, page, size);

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Compte introuvable avec l'ID : " + accountId));

        Page<AccountOperation> accountOperations = accountOperationRepository
                .findByBankAccountId(accountId, PageRequest.of(page, size));

        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        accountHistoryDTO.setAccountId(accountId);
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        accountHistoryDTO.setAccountOperationDTOs(
                accountOperations.getContent()
                        .stream()
                        .map(dtoMapper::fromAccountOperation)
                        .collect(Collectors.toList())
        );
        return accountHistoryDTO;
    }

    // ====================================================================
    // Utilitaire : Audit / SecurityContext
    // ====================================================================

    /**
     * Récupère le nom de l'utilisateur authentifié depuis le SecurityContext.
     * Retourne "anonymous" si aucune authentification n'est présente
     * (ex: avant l'activation de Spring Security en Partie 3).
     */
    private String getAuthenticatedUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception ignored) {
            // Pas de contexte de sécurité disponible
        }
        return "anonymous";
    }
}
