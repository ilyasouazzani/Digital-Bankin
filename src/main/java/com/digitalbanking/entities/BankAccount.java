package com.digitalbanking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Entité abstraite représentant un compte bancaire.
 *
 * Stratégie d'héritage SINGLE_TABLE :
 *   - Toutes les sous-classes (CurrentAccount, SavingAccount) partagent
 *     la même table "bank_accounts".
 *   - La colonne discriminante "TYPE" (4 caractères) distingue le type de compte.
 *
 * Avantages : pas de JOIN coûteux, performances en lecture optimales.
 * Inconvénient : colonnes spécifiques aux sous-classes sont nullable.
 */
@Entity
@Table(name = "bank_accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = 4)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BankAccount {

    /**
     * Identifiant UUID généré applicativement (String) pour garantir
     * l'unicité même en environnement distribué.
     */
    @Id
    private String id;

    /**
     * Solde courant du compte.
     */
    @Column(nullable = false)
    private double balance;

    /**
     * Date de création du compte.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /**
     * Statut du compte : CREATED, ACTIVATED, SUSPENDED.
     */
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    /**
     * Devise du compte (ex: "EUR", "USD").
     */
    @Column(length = 3)
    private String currency;

    /**
     * Propriétaire du compte (relation ManyToOne vers Customer).
     * La clé étrangère "customer_id" est stockée dans la table "bank_accounts".
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * Liste des opérations effectuées sur ce compte.
     * Chargement LAZY pour éviter des requêtes inutiles.
     */
    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountOperation> accountOperations;
}
