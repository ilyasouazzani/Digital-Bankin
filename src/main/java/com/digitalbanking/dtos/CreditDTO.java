package com.digitalbanking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO pour une opération de crédit (versement).
 */
@Data
public class CreditDTO {

    @NotBlank(message = "L'identifiant du compte est obligatoire")
    private String accountId;

    @Positive(message = "Le montant doit être strictement positif")
    private double amount;

    @NotBlank(message = "La description est obligatoire")
    private String description;
}
