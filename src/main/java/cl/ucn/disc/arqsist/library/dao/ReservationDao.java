/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Reservation;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Data access for {@link Reservation} entities.
 */
public final class ReservationDao extends BaseDao<Reservation> {

    /**
     * Creates the DAO for the reservations table.
     *
     * @param connectionSource the database connection source.
     */
    public ReservationDao(ConnectionSource connectionSource) {
        super(connectionSource, Reservation.class);
    }
}
