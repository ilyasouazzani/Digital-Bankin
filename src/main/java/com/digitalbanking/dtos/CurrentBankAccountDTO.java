package com.digitalbanking.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour un compte courant (CurrentAccount).
 * Étend BankAccountDTO avec le montant du découvert autorisé.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CurrentBankAccountDTO extends BankAccountDTO {
    /** Découvert autorisé en unité monétaire. */
    private double overDraft;
}
