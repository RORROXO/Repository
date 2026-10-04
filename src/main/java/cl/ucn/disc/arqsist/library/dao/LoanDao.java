/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Loan;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Data access for {@link Loan} entities.
 */
public final class LoanDao extends BaseDao<Loan> {

    /**
     * Creates the DAO for the loans table.
     *
     * @param connectionSource the database connection source.
     */
    public LoanDao(ConnectionSource connectionSource) {
        super(connectionSource, Loan.class);
    }
}
