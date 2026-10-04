/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Book;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Data access for {@link Book} entities.
 */
public final class BookDao extends BaseDao<Book> {

    /**
     * Creates the DAO for the books table.
     *
     * @param connectionSource the database connection source.
     */
    public BookDao(ConnectionSource connectionSource) {
        super(connectionSource, Book.class);
    }
}
