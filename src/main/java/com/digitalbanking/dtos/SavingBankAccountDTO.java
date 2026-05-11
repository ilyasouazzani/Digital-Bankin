package com.digitalbanking.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour un compte épargne (SavingAccount).
 * Étend BankAccountDTO avec le taux d'intérêt annuel.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SavingBankAccountDTO extends BankAccountDTO {
    /** Taux d'intérêt annuel en pourcentage (ex: 5.5 pour 5,5%). */
    private double interestRate;
}
