package com.digitalbanking.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Compte épargne (Saving Account).
 *
 * Discriminator value "SA" dans la colonne TYPE de la table bank_accounts.
 * Propriété spécifique : taux d'intérêt annuel (interestRate).
 */
@Entity
@DiscriminatorValue("SA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SavingAccount extends BankAccount {

    /**
     * Taux d'intérêt annuel en pourcentage (ex: 5.5 pour 5,5%).
     */
    private double interestRate;
}
