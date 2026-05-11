package com.digitalbanking.mappers;

import com.digitalbanking.dtos.*;
import com.digitalbanking.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Composant de mapping entre les entités JPA et les DTOs.
 *
 * Utilise Spring's BeanUtils.copyProperties() pour la copie des propriétés
 * communes, puis effectue le mapping des relations manuellement.
 *
 * Pourquoi ne pas utiliser MapStruct directement ici ?
 *  - BeanUtils est plus simple et évite la génération de code pour ce cas.
 *  - MapStruct est configuré dans le pom.xml et peut être activé facilement.
 */
@Service
public class BankAccountMapperImpl {

    // ====================================================================
    // Customer ↔ CustomerDTO
    // ====================================================================

    /**
     * Convertit une entité Customer en CustomerDTO.
     */
    public CustomerDTO fromCustomer(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

    /**
     * Convertit un CustomerDTO en entité Customer.
     */
    public Customer fromCustomerDTO(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

    // ====================================================================
    // SavingAccount ↔ SavingBankAccountDTO
    // ====================================================================

    /**
     * Convertit un SavingAccount en SavingBankAccountDTO.
     */
    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount) {
        SavingBankAccountDTO savingBankAccountDTO = new SavingBankAccountDTO();
        BeanUtils.copyProperties(savingAccount, savingBankAccountDTO);
        // Mapping manuel du Customer imbriqué
        CustomerDTO customerDTO = fromCustomer(savingAccount.getCustomer());
        savingBankAccountDTO.setCustomerDTO(customerDTO);
        // Discriminateur de type pour le frontend
        savingBankAccountDTO.setType("SavingAccount");
        return savingBankAccountDTO;
    }

    /**
     * Convertit un SavingBankAccountDTO en entité SavingAccount.
     */
    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO savingBankAccountDTO) {
        SavingAccount savingAccount = new SavingAccount();
        BeanUtils.copyProperties(savingBankAccountDTO, savingAccount);
        Customer customer = fromCustomerDTO(savingBankAccountDTO.getCustomerDTO());
        savingAccount.setCustomer(customer);
        return savingAccount;
    }

    // ====================================================================
    // CurrentAccount ↔ CurrentBankAccountDTO
    // ====================================================================

    /**
     * Convertit un CurrentAccount en CurrentBankAccountDTO.
     */
    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount) {
        CurrentBankAccountDTO currentBankAccountDTO = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(currentAccount, currentBankAccountDTO);
        CustomerDTO customerDTO = fromCustomer(currentAccount.getCustomer());
        currentBankAccountDTO.setCustomerDTO(customerDTO);
        currentBankAccountDTO.setType("CurrentAccount");
        return currentBankAccountDTO;
    }

    /**
     * Convertit un CurrentBankAccountDTO en entité CurrentAccount.
     */
    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO) {
        CurrentAccount currentAccount = new CurrentAccount();
        BeanUtils.copyProperties(currentBankAccountDTO, currentAccount);
        Customer customer = fromCustomerDTO(currentBankAccountDTO.getCustomerDTO());
        currentAccount.setCustomer(customer);
        return currentAccount;
    }

    // ====================================================================
    // AccountOperation ↔ AccountOperationDTO
    // ====================================================================

    /**
     * Convertit une AccountOperation en AccountOperationDTO.
     */
    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation) {
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation, accountOperationDTO);
        return accountOperationDTO;
    }
}
