package com.digitalbanking.dtos;

import com.digitalbanking.entities.AccountStatus;
import lombok.Data;

import java.util.Date;

/**
 * DTO de base pour un compte bancaire.
 * Contient les champs communs à CurrentBankAccountDTO et SavingBankAccountDTO.
 *
 * Le champ "type" permet au frontend de distinguer le type de compte
 * sans avoir à inspecter les champs spécifiques.
 */
@Data
public class BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private String currency;
    private CustomerDTO customerDTO;
    /** "CurrentAccount" ou "SavingAccount" — positionné par le mapper. */
    private String type;
}
