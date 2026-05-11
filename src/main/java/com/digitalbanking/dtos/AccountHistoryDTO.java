package com.digitalbanking.dtos;

import lombok.Data;

import java.util.List;

/**
 * DTO encapsulant l'historique paginé des opérations d'un compte bancaire.
 *
 * Contient à la fois :
 *  - les métadonnées de pagination (numéro de page, taille, total des pages)
 *  - l'identifiant et le solde du compte concerné
 *  - la liste des opérations de la page courante
 */
@Data
public class AccountHistoryDTO {

    /** Identifiant du compte bancaire. */
    private String accountId;

    /** Solde actuel du compte. */
    private double balance;

    /** Numéro de la page courante (0-indexed). */
    private int currentPage;

    /** Nombre d'éléments par page. */
    private int pageSize;

    /** Nombre total de pages disponibles. */
    private int totalPages;

    /** Liste des opérations de la page courante. */
    private List<AccountOperationDTO> accountOperationDTOs;
}
