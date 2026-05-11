package com.digitalbanking;

import com.digitalbanking.dtos.CustomerDTO;
import com.digitalbanking.exceptions.CustomerNotFoundException;
import com.digitalbanking.security.services.AccountService;
import com.digitalbanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService,
                                       AccountService accountService) {
        return args -> {

            // ---- Rôles ----
            accountService.addNewRole("ROLE_USER");
            accountService.addNewRole("ROLE_ADMIN");

            // ---- Utilisateurs ----
            accountService.addNewUser("admin",  "admin1234",  "admin@digitalbank.fr");
            accountService.addNewUser("user1",  "user1234",   "user1@digitalbank.fr");
            accountService.addRoleToUser("admin", "ROLE_ADMIN");
            accountService.addRoleToUser("admin", "ROLE_USER");
            accountService.addRoleToUser("user1", "ROLE_USER");

            // ---- Clients bancaires de démo ----
            Stream.of("Alice Martin", "Bob Dupont", "Charlie Leblanc").forEach(name -> {
                CustomerDTO dto = new CustomerDTO();
                dto.setName(name);
                dto.setEmail(name.toLowerCase().replace(" ", ".") + "@digitalbank.fr");
                bankAccountService.saveCustomer(dto);
            });

            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, customer.getId());
                } catch (CustomerNotFoundException e) { e.printStackTrace(); }
            });

            bankAccountService.bankAccountList().forEach(account -> {
                try {
                    for (int i = 0; i < 10; i++) {
                        bankAccountService.credit(account.getId(), 10000 + Math.random() * 50000, "Crédit #" + i);
                        bankAccountService.debit(account.getId(),  1000  + Math.random() * 9000,  "Débit #" + i);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });

            System.out.println("================================================");
            System.out.println("  Digital Banking App — données initialisées ✓  ");
            System.out.println("  Swagger  : http://localhost:8085/swagger-ui.html");
            System.out.println("  H2       : http://localhost:8085/h2-console     ");
            System.out.println("  Login    : POST /api/auth/login                 ");
            System.out.println("  admin / admin1234  |  user1 / user1234          ");
            System.out.println("================================================");
        };
    }
}
