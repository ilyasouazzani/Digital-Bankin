package com.digitalbanking.exceptions;

/**
 * Exception levée lorsqu'un client est introuvable en base de données.
 */
public class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
