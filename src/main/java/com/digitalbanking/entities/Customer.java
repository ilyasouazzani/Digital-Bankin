package com.digitalbanking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité représentant un client de la banque.
 */
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Un client peut posséder plusieurs comptes bancaires.
     * mappedBy fait référence au champ "customer" dans BankAccount.
     * CascadeType.ALL : toute opération sur Customer se répercute sur ses comptes.
     * FetchType.LAZY : les comptes ne sont chargés qu'à la demande (performance).
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BankAccount> bankAccounts;
}
