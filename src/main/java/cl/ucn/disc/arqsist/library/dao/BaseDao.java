/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Shared CRUD and transaction support for the entity DAOs.
 *
 * @param <T> the entity type, with an {@link Integer} id.
 */
public abstract class BaseDao<T> {

    /**
     * The ORMLite DAO that performs the database operations.
     */
    protected final Dao<T, Integer> dao;

    /**
     * Creates the ORMLite DAO for the given entity class.
     *
     * @param connectionSource the database connection source.
     * @param clazz            the entity class.
     * @throws RuntimeException if the DAO cannot be created.
     */
    protected BaseDao(ConnectionSource connectionSource, Class<T> clazz) {
        try {
            this.dao = DaoManager.createDao(connectionSource, clazz);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns every entity.
     *
     * @return the list of entities, empty when there are none.
     * @throws RuntimeException if the query fails.
     */
    public List<T> findAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the entity with the given id.
     *
     * @param id the entity id.
     * @return the entity, or null when no entity has that id.
     * @throws RuntimeException if the query fails.
     */
    public T findById(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a new entity and sets its generated id.
     *
     * @param entity the entity to insert.
     * @throws RuntimeException if the insert fails.
     */
    public void create(T entity) {
        try {
            dao.create(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an existing entity.
     *
     * @param entity the entity to update.
     * @throws RuntimeException if the update fails.
     */
    public void update(T entity) {
        try {
            dao.update(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes an existing entity.
     *
     * @param entity the entity to delete.
     * @throws RuntimeException if the delete fails.
     */
    public void delete(T entity) {
        try {
            dao.delete(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs the callable in one database transaction. The transaction commits when the callable returns and rolls
     * back when it throws. A {@link RuntimeException} thrown by the callable (for example a NotFoundException)
     * reaches the caller unwrapped.
     *
     * @param callable the work to run in the transaction.
     * @param <R>      the result type.
     * @return the value returned by the callable.
     * @throws SQLException if the transaction fails for a database reason.
     */
    public <R> R transaction(Callable<R> callable) throws SQLException {
        try {
            return TransactionManager.callInTransaction(dao.getConnectionSource(), callable);
        } catch (SQLException e) {
            if (e.getCause() instanceof RuntimeException cause) {
                throw cause;
            }
            throw e;
        }
    }
}
