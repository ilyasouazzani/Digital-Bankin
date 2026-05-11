package com.digitalbanking.chatbot.services;

import com.digitalbanking.repositories.AccountOperationRepository;
import com.digitalbanking.repositories.BankAccountRepository;
import com.digitalbanking.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service exposant le contexte bancaire au RAG (Function Calling).
 * Permet au chatbot d'interroger les données réelles de la banque.
 */
@Service
@AllArgsConstructor
public class BankingContextService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    /**
     * Retourne un résumé statistique général de la banque.
     */
    @Transactional(readOnly = true)
    public String getBankingSummary() {
        long customerCount = customerRepository.count();
        long accountCount  = bankAccountRepository.count();
        long operationCount = accountOperationRepository.count();
        double totalBalance = bankAccountRepository.findAll()
                .stream().mapToDouble(a -> a.getBalance()).sum();

        return String.format(
            "Résumé Digital Banking : %d clients, %d comptes actifs, %d opérations enregistrées, " +
            "solde total géré : %.2f EUR.",
            customerCount, accountCount, operationCount, totalBalance);
    }

    /**
     * Recherche le solde d'un compte par son identifiant UUID.
     */
    @Transactional(readOnly = true)
    public String getAccountBalance(String accountId) {
        return bankAccountRepository.findById(accountId)
                .map(a -> String.format("Compte %s : solde = %.2f %s, statut = %s",
                        accountId, a.getBalance(), a.getCurrency(), a.getStatus()))
                .orElse("Aucun compte trouvé avec l'identifiant : " + accountId);
    }

    /**
     * Retourne les informations d'un client par son nom.
     */
    @Transactional(readOnly = true)
    public String getCustomerInfo(String name) {
        var customers = customerRepository.findByNameContainsIgnoreCase(name);
        if (customers.isEmpty()) return "Aucun client trouvé avec le nom : " + name;
        StringBuilder sb = new StringBuilder();
        customers.forEach(c -> sb.append(String.format(
            "Client %s (ID:%d, email:%s) — %d compte(s). ",
            c.getName(), c.getId(), c.getEmail(),
            c.getBankAccounts() != null ? c.getBankAccounts().size() : 0)));
        return sb.toString();
    }

    /**
     * Retourne le contexte documentaire statique de la banque pour le RAG.
     * Ce texte est utilisé pour l'embedding et la recherche vectorielle.
     */
    public String getBankingKnowledgeBase() {
        return """
            === DIGITAL BANKING - BASE DE CONNAISSANCES ===

            TYPES DE COMPTES :
            - Compte Courant (CurrentAccount) : compte de dépôt quotidien avec découvert autorisé.
              Le découvert permet d'aller en dessous de zéro jusqu'à la limite fixée.
            - Compte Épargne (SavingAccount) : compte rémunéré avec taux d'intérêt annuel.
              Les intérêts sont calculés sur le solde moyen annuel.

            OPÉRATIONS DISPONIBLES :
            - CREDIT (Versement) : ajoute un montant au solde du compte.
            - DEBIT (Retrait) : soustrait un montant du solde (limité par solde + découvert pour courant).
            - TRANSFER (Virement) : débit du compte source + crédit du compte destination. Opération atomique.

            STATUTS DE COMPTE :
            - CREATED : compte nouvellement créé, pas encore activé.
            - ACTIVATED : compte opérationnel, toutes opérations autorisées.
            - SUSPENDED : compte suspendu, aucune opération possible.

            SÉCURITÉ :
            - Authentification par JWT (JSON Web Token), valide 24h.
            - Chaque opération est tracée avec l'identifiant de l'utilisateur (audit).
            - Les mots de passe sont hashés en BCrypt.

            CONTACT ET SUPPORT :
            - Pour toute question, contacter support@digitalbank.fr
            - Horaires : Lundi-Vendredi 8h-18h
            """;
    }
}
