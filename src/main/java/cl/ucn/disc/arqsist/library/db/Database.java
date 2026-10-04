/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.db;

import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * The SQLite database: opens the connection, creates the tables, and seeds the sample data.
 */
public final class Database {

    /**
     * The logger of this class.
     */
    private static final Logger log = LoggerFactory.getLogger(Database.class);

    /**
     * The connection source shared by every DAO.
     */
    private final ConnectionSource connectionSource;

    /**
     * Opens the database and creates any missing table.
     *
     * @param jdbcUrl the JDBC URL of the database, for example jdbc:sqlite:database.db.
     * @throws SQLException if the connection or a table creation fails.
     */
    public Database(String jdbcUrl) throws SQLException {
        this.connectionSource = new JdbcConnectionSource(jdbcUrl);
        TableUtils.createTableIfNotExists(connectionSource, Book.class);
        TableUtils.createTableIfNotExists(connectionSource, Member.class);
        TableUtils.createTableIfNotExists(connectionSource, Loan.class);
        TableUtils.createTableIfNotExists(connectionSource, Reservation.class);
    }

    /**
     * Returns the connection source shared by every DAO.
     *
     * @return the connection source.
     */
    public ConnectionSource connectionSource() {
        return connectionSource;
    }

    /**
     * Seeds the sample data. Each table is seeded only when it is empty, so a second call changes nothing.
     * The seed creates three books, three members, three loans (one returned, one active, and one overdue),
     * and one reservation. Only the active and the overdue loans take a copy out of the stock.
     *
     * @throws SQLException if a query or an insert fails.
     */
    public void seedIfEmpty() throws SQLException {
        Dao<Book, Integer> bookDao = DaoManager.createDao(connectionSource, Book.class);
        if (bookDao.queryForAll().isEmpty()) {
            bookDao.create(new Book("Clean Code", "Robert C. Martin", "9780132350884", 3));
            bookDao.create(new Book("The Pragmatic Programmer", "Hunt & Thomas", "9780201616224", 2));
            bookDao.create(new Book("Design Patterns", "Gamma et al.", "9780201633610", 4));
            log.debug("Seeded 3 books");
        }

        Dao<Member, Integer> memberDao = DaoManager.createDao(connectionSource, Member.class);
        if (memberDao.queryForAll().isEmpty()) {
            memberDao.create(new Member("Ada Lovelace", "ada@example.com"));
            memberDao.create(new Member("Grace Hopper", "grace@example.com"));
            memberDao.create(new Member("Alan Turing", "alan@example.com"));
            log.debug("Seeded 3 members");
        }

        Dao<Loan, Integer> loanDao = DaoManager.createDao(connectionSource, Loan.class);
        if (loanDao.queryForAll().isEmpty()) {
            List<Book> books = bookDao.queryForAll();
            List<Member> members = memberDao.queryForAll();
            LocalDate today = LocalDate.now();

            // returned: the copy is back on the shelf, so the stock does not change
            Loan returned = new Loan(members.getFirst(), books.getFirst(), today.minusDays(30), today.minusDays(9));
            returned.setReturned(true);
            returned.setReturnDate(today.minusDays(10));
            loanDao.create(returned);
            log.debug("Seeded returned loan {} of book {}", returned.getId(), books.getFirst().getId());

            // active: due in the future, takes one copy
            Book activeBook = books.get(2);
            Loan active = new Loan(members.get(1), activeBook, today.minusDays(2), today.plusDays(19));
            loanDao.create(active);
            activeBook.setAvailableCopies(activeBook.getAvailableCopies() - 1);
            bookDao.update(activeBook);
            log.debug("Seeded active loan {} of book {}, {} copies left",
                    active.getId(), activeBook.getId(), activeBook.getAvailableCopies());

            // overdue: due in the past and not returned, takes one copy
            Book overdueBook = books.get(1);
            Loan overdue = new Loan(members.get(2), overdueBook, today.minusDays(30), today.minusDays(9));
            loanDao.create(overdue);
            overdueBook.setAvailableCopies(overdueBook.getAvailableCopies() - 1);
            bookDao.update(overdueBook);
            log.debug("Seeded overdue loan {} of book {}, {} copies left",
                    overdue.getId(), overdueBook.getId(), overdueBook.getAvailableCopies());
        }

        Dao<Reservation, Integer> reservationDao = DaoManager.createDao(connectionSource, Reservation.class);
        if (reservationDao.queryForAll().isEmpty()) {
            List<Book> books = bookDao.queryForAll();
            List<Member> members = memberDao.queryForAll();
            LocalDate today = LocalDate.now();

            Reservation reservation = new Reservation(members.getFirst(), books.get(1), today.minusDays(1));
            reservationDao.create(reservation);
            log.debug("Seeded reservation {} of book {}", reservation.getId(), books.get(1).getId());
        }
    }
}
