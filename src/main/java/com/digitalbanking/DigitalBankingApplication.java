package com.digitalbanking;

import com.digitalbanking.dtos.CustomerDTO;
import com.digitalbanking.entities.AccountStatus;
import com.digitalbanking.entities.CurrentAccount;
import com.digitalbanking.entities.SavingAccount;
import com.digitalbanking.exceptions.CustomerNotFoundException;
import com.digitalbanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingApplication.class, args);
    }

    /**
     * Initialisation des données de démonstration au démarrage.
     * À retirer ou protéger en production.
     */
    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            // Création de clients de démonstration
            Stream.of("Alice Martin", "Bob Dupont", "Charlie Leblanc").forEach(name -> {
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setName(name);
                customerDTO.setEmail(name.toLowerCase().replace(" ", ".") + "@digitalbank.fr");
                bankAccountService.saveCustomer(customerDTO);
            });

            // Création de comptes pour chaque client
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    // Compte courant avec un découvert de 9000
                    bankAccountService.saveCurrentBankAccount(
                            Math.random() * 90000,
                            9000,
                            customer.getId()
                    );
                    // Compte épargne avec taux d'intérêt à 5.5%
                    bankAccountService.saveSavingBankAccount(
                            Math.random() * 120000,
                            5.5,
                            customer.getId()
                    );

                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });

            // Simulation d'opérations sur chaque compte
            bankAccountService.bankAccountList().forEach(bankAccount -> {
                try {
                    for (int i = 0; i < 10; i++) {
                        String accountId = bankAccount.getId();
                        bankAccountService.credit(accountId, 10000 + Math.random() * 120000,
                                "Virement entrant #" + i);
                        bankAccountService.debit(accountId, 1000 + Math.random() * 9000,
                                "Retrait #" + i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            System.out.println("=================================================");
            System.out.println(" Digital Banking App - Données initialisées OK  ");
            System.out.println(" Swagger UI : http://localhost:8085/swagger-ui.html");
            System.out.println(" H2 Console : http://localhost:8085/h2-console    ");
            System.out.println("=================================================");
        };
    }
}
