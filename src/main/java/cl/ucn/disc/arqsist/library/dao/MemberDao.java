/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Member;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Data access for {@link Member} entities.
 */
public final class MemberDao extends BaseDao<Member> {

    /**
     * Creates the DAO for the members table.
     *
     * @param connectionSource the database connection source.
     */
    public MemberDao(ConnectionSource connectionSource) {
        super(connectionSource, Member.class);
    }

    /**
     * Finds members whose name contains the literal query, ignoring ASCII case.
     *
     * @param query the name fragment
     * @return the matching members in ascending ID order
     * @throws RuntimeException if the query fails
     */
    public List<Member> searchByName(String query) {
        try {
            var builder = dao.queryBuilder().orderBy("id", true);
            builder.where().raw("instr(lower(name), lower(?)) > 0", new SelectArg("name", query));
            return builder.query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
