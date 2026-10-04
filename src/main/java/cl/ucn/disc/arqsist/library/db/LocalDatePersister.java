/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.db;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * ORMLite persister that stores a {@link LocalDate} as an ISO-8601 text column (yyyy-MM-dd).
 */
public final class LocalDatePersister extends BaseDataType {

    /**
     * The single shared instance, returned to ORMLite by {@link #getSingleton()}.
     */
    private static final LocalDatePersister SINGLETON = new LocalDatePersister();

    /**
     * Creates the persister for the STRING SQL type and the {@link LocalDate} class.
     */
    private LocalDatePersister() {
        super(SqlType.STRING, new Class<?>[]{LocalDate.class});
    }

    /**
     * Returns the shared instance. ORMLite calls this method through the persisterClass attribute.
     *
     * @return the singleton persister.
     */
    public static LocalDatePersister getSingleton() {
        return SINGLETON;
    }

    /**
     * Converts the default value declared in the field annotation.
     *
     * @param fieldType  the field being converted.
     * @param defaultStr the default value as text.
     * @return the default value unchanged.
     */
    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return defaultStr;
    }

    /**
     * Reads the raw column value from a result row.
     *
     * @param fieldType the field being read.
     * @param results   the database results.
     * @param columnPos the column position in the results.
     * @return the column value as text, or null when the column is null.
     * @throws SQLException if the column cannot be read.
     */
    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    /**
     * Converts the column text into a {@link LocalDate}.
     *
     * @param fieldType the field being converted.
     * @param sqlArg    the column value as text.
     * @param columnPos the column position in the results.
     * @return the parsed date.
     */
    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return LocalDate.parse((String) sqlArg);
    }

    /**
     * Converts a {@link LocalDate} into the text stored in the column.
     *
     * @param fieldType  the field being converted.
     * @param javaObject the date to store.
     * @return the date in ISO-8601 format (yyyy-MM-dd).
     */
    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        return javaObject.toString();
    }
}
