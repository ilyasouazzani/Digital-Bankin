package com.digitalbanking.dtos;

import com.digitalbanking.entities.OperationType;
import lombok.Data;

import java.util.Date;

/**
 * DTO représentant une opération bancaire.
 * Utilisé dans la liste d'historique d'un compte.
 */
@Data
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
    /** ID de l'utilisateur ayant effectué l'opération (audit, Partie 3). */
    private String performedBy;
}
