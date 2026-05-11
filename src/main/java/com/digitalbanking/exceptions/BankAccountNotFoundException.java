package com.digitalbanking.exceptions;

/**
 * Exception levée lorsqu'un compte bancaire est introuvable en base de données.
 */
public class BankAccountNotFoundException extends Exception {

    public BankAccountNotFoundException(String message) {
        super(message);
    }
}
