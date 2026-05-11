package com.digitalbanking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO pour un virement entre deux comptes (transfer).
 * Contient les comptes source et destination ainsi que le montant.
 */
@Data
public class TransferRequestDTO {

    @NotBlank(message = "Le compte source est obligatoire")
    private String accountSource;

    @NotBlank(message = "Le compte destination est obligatoire")
    private String accountDestination;

    @Positive(message = "Le montant doit être strictement positif")
    private double amount;

    private String description;
}
