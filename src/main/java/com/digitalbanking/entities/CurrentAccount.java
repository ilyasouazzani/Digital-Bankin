package com.digitalbanking.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Compte courant (Current Account).
 *
 * Discriminator value "CA" dans la colonne TYPE de la table bank_accounts.
 * Propriété spécifique : découvert autorisé (overDraft).
 */
@Entity
@DiscriminatorValue("CA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CurrentAccount extends BankAccount {

    /**
     * Montant du découvert autorisé (en unité monétaire).
     * Permet au solde de descendre en dessous de 0 jusqu'à ce seuil.
     */
    private double overDraft;
}
