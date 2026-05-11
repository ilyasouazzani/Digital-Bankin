package com.digitalbanking.exceptions;

/**
 * Exception levée lorsque le solde d'un compte est insuffisant
 * pour effectuer un débit ou un virement.
 * Tient compte du découvert autorisé pour les comptes courants.
 */
public class BalanceNotSufficientException extends Exception {

    public BalanceNotSufficientException(String message) {
        super(message);
    }
}
