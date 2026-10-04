/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

/**
 * Signals that a requested record (book, member, loan, or reservation) does not exist.
 */
public final class NotFoundException extends RuntimeException {

    /**
     * Creates a new exception with the given detail message.
     *
     * @param message the detail message that names the missing record.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
