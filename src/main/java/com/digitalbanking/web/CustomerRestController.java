package com.digitalbanking.web;

import com.digitalbanking.dtos.CustomerDTO;
import com.digitalbanking.exceptions.CustomerNotFoundException;
import com.digitalbanking.services.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller pour la gestion des clients.
 *
 * Expose les endpoints CRUD sur /api/customers.
 * Les annotations Swagger documentent chaque endpoint pour OpenAPI.
 */
@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
@Slf4j
@Tag(name = "Customers", description = "API de gestion des clients")
@CrossOrigin(origins = "*")
public class CustomerRestController {

    private final BankAccountService bankAccountService;

    // ------------------------------------------------------------------
    // GET /api/customers — Liste de tous les clients
    // ------------------------------------------------------------------

    @GetMapping
    @Operation(summary = "Récupère la liste de tous les clients",
               description = "Retourne l'ensemble des clients. Accepte un paramètre de recherche optionnel.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    public List<CustomerDTO> listCustomers(
            @Parameter(description = "Mot-clé de recherche sur le nom (optionnel)")
            @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        if (keyword.isBlank()) {
            return bankAccountService.listCustomers();
        } else {
            return bankAccountService.searchCustomers(keyword);
        }
    }

    // ------------------------------------------------------------------
    // GET /api/customers/{id} — Détail d'un client
    // ------------------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Récupère un client par son ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Client trouvé"),
        @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id)
            throws CustomerNotFoundException {
        return ResponseEntity.ok(bankAccountService.getCustomer(id));
    }

    // ------------------------------------------------------------------
    // POST /api/customers — Création d'un client
    // ------------------------------------------------------------------

    @PostMapping
    @Operation(summary = "Crée un nouveau client")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<CustomerDTO> saveCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bankAccountService.saveCustomer(customerDTO));
    }

    // ------------------------------------------------------------------
    // PUT /api/customers/{id} — Mise à jour d'un client
    // ------------------------------------------------------------------

    @PutMapping("/{id}")
    @Operation(summary = "Met à jour les informations d'un client")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Client mis à jour"),
        @ApiResponse(responseCode = "404", description = "Client introuvable"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        return ResponseEntity.ok(bankAccountService.updateCustomer(id, customerDTO));
    }

    // ------------------------------------------------------------------
    // DELETE /api/customers/{id} — Suppression d'un client
    // ------------------------------------------------------------------

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un client par son ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Client supprimé"),
        @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id)
            throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
