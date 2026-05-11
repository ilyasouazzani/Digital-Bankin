package com.digitalbanking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entité représentant une opération (débit ou crédit) sur un compte bancaire.
 */
@Entity
@Table(name = "account_operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date et heure de l'opération.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date operationDate;

    /**
     * Montant de l'opération (toujours positif ; le type détermine le sens).
     */
    @Column(nullable = false)
    private double amount;

    /**
     * Type de l'opération : DEBIT ou CREDIT.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    private OperationType type;

    /**
     * Description libre de l'opération (motif de virement, libellé...).
     */
    @Column(length = 255)
    private String description;

    /**
     * Compte bancaire auquel cette opération est rattachée.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    /**
     * (Partie 3 - Audit) : Identifiant de l'utilisateur authentifié
     * ayant initié cette opération. Alimenté via le SecurityContext.
     */
    @Column(name = "performed_by", length = 150)
    private String performedBy;
}
