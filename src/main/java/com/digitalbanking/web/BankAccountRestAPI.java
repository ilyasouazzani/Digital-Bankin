package com.digitalbanking.web;

import com.digitalbanking.dtos.*;
import com.digitalbanking.exceptions.BalanceNotSufficientException;
import com.digitalbanking.exceptions.BankAccountNotFoundException;
import com.digitalbanking.services.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller pour la gestion des comptes bancaires et des opérations.
 *
 * Expose les endpoints sur /api/accounts.
 */
@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Slf4j
@Tag(name = "Bank Accounts", description = "API de gestion des comptes bancaires et opérations")
@CrossOrigin(origins = "*")
public class BankAccountRestAPI {

    private final BankAccountService bankAccountService;

    // ------------------------------------------------------------------
    // GET /api/accounts — Liste de tous les comptes
    // ------------------------------------------------------------------

    @GetMapping
    @Operation(summary = "Récupère la liste de tous les comptes bancaires")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    public List<BankAccountDTO> listBankAccounts() {
        return bankAccountService.bankAccountList();
    }

    // ------------------------------------------------------------------
    // GET /api/accounts/{accountId} — Détail d'un compte
    // ------------------------------------------------------------------

    @GetMapping("/{accountId}")
    @Operation(summary = "Récupère un compte bancaire par son ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compte trouvé"),
        @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    public BankAccountDTO getBankAccount(
            @Parameter(description = "UUID du compte bancaire", required = true)
            @PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    // ------------------------------------------------------------------
    // GET /api/accounts/customer/{customerId} — Comptes d'un client
    // ------------------------------------------------------------------

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Récupère tous les comptes d'un client")
    @ApiResponse(responseCode = "200", description = "Comptes récupérés avec succès")
    public List<BankAccountDTO> getCustomerAccounts(@PathVariable Long customerId) {
        return bankAccountService.getCustomerAccounts(customerId);
    }

    // ------------------------------------------------------------------
    // GET /api/accounts/{accountId}/operations — Historique complet
    // ------------------------------------------------------------------

    @GetMapping("/{accountId}/operations")
    @Operation(summary = "Récupère l'historique complet des opérations d'un compte")
    @ApiResponse(responseCode = "200", description = "Historique récupéré")
    public List<AccountOperationDTO> getAccountOperations(@PathVariable String accountId) {
        return bankAccountService.accountHistory(accountId);
    }

    // ------------------------------------------------------------------
    // GET /api/accounts/{accountId}/pageOperations — Historique paginé
    // ------------------------------------------------------------------

    @GetMapping("/{accountId}/pageOperations")
    @Operation(summary = "Récupère l'historique paginé des opérations d'un compte",
               description = "Utilise la pagination Spring Data. page=0 pour la première page.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Page d'opérations récupérée"),
        @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    public AccountHistoryDTO getAccountHistory(
            @PathVariable String accountId,
            @Parameter(description = "Numéro de page (0-indexed)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "5")
            @RequestParam(name = "size", defaultValue = "5") int size)
            throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }

    // ------------------------------------------------------------------
    // POST /api/accounts/debit — Opération de débit
    // ------------------------------------------------------------------

    @PostMapping("/debit")
    @Operation(summary = "Effectue un débit sur un compte")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Débit effectué avec succès"),
        @ApiResponse(responseCode = "400", description = "Solde insuffisant ou données invalides"),
        @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    public ResponseEntity<DebitDTO> debit(@Valid @RequestBody DebitDTO debitDTO)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
        return ResponseEntity.ok(debitDTO);
    }

    // ------------------------------------------------------------------
    // POST /api/accounts/credit — Opération de crédit
    // ------------------------------------------------------------------

    @PostMapping("/credit")
    @Operation(summary = "Effectue un crédit sur un compte")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Crédit effectué avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    public ResponseEntity<CreditDTO> credit(@Valid @RequestBody CreditDTO creditDTO)
            throws BankAccountNotFoundException {
        bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
        return ResponseEntity.ok(creditDTO);
    }

    // ------------------------------------------------------------------
    // POST /api/accounts/transfer — Virement entre comptes
    // ------------------------------------------------------------------

    @PostMapping("/transfer")
    @Operation(summary = "Effectue un virement entre deux comptes",
               description = "Opération atomique : débit du compte source + crédit du compte destination.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Virement effectué avec succès"),
        @ApiResponse(responseCode = "400", description = "Solde insuffisant ou données invalides"),
        @ApiResponse(responseCode = "404", description = "Compte source ou destination introuvable")
    })
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequestDTO transferRequestDTO)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.transfer(
                transferRequestDTO.getAccountSource(),
                transferRequestDTO.getAccountDestination(),
                transferRequestDTO.getAmount()
        );
        return ResponseEntity.ok().build();
    }
}
