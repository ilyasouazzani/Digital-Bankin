package com.digitalbanking.dtos;

import lombok.Data;

/**
 * DTO pour l'entité Customer.
 * Utilisé pour toutes les opérations CRUD exposées par l'API REST.
 * Les entités ne sont jamais exposées directement.
 */
@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
}
