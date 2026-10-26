/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Detects Errors #7 and #8 in the seed: every open loan must take a copy out of stock, and a second seed must
 * change nothing.
 */
class DatabaseSeedTest {

    /**
     * The in-memory database under test.
     */
    private Database db;

    /**
     * The DAO of the books table.
     */
    private BookDao bookDao;

    /**
     * The DAO of the loans table.
     */
    private LoanDao loanDao;

    /**
     * The DAO of the reservations table.
     */
    private ReservationDao reservationDao;

    /**
     * Creates an empty in-memory database and the DAOs.
     *
     * @throws Exception if the database cannot be created.
     */
    @BeforeEach
    void setUp() throws Exception {
        db = new Database("jdbc:sqlite::memory:");
        bookDao = new BookDao(db.connectionSource());
        loanDao = new LoanDao(db.connectionSource());
        reservationDao = new ReservationDao(db.connectionSource());
    }

    /**
     * The seed creates loans and reservations, exactly one overdue loan, and one copy out of stock for every
     * book with an open loan.
     *
     * @throws Exception if the seed fails.
     */
    @Test
    void seedCreatesConsistentLoansAndReservations() throws Exception {
        db.seedIfEmpty();

        List<Loan> loans = loanDao.findAll();
        assertFalse(loans.isEmpty(), "the seed should create loans");
        assertFalse(reservationDao.findAll().isEmpty(), "the seed should create reservations");

        LocalDate today = LocalDate.now();
        long overdue = loans.stream()
                .filter(loan -> !loan.isReturned() && loan.getDueDate().isBefore(today))
                .count();
        assertEquals(1, overdue, "the seed should create one overdue loan");

        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                Book book = bookDao.findById(loan.getBook().getId());
                assertTrue(book.getAvailableCopies() < book.getTotalCopies(),
                        "book " + book.getId() + " has an open loan but no copy out of stock");
            }
        }
    }

    /**
     * A second seed keeps the number of loans and reservations.
     *
     * @throws Exception if the seed fails.
     */
    @Test
    void secondSeedChangesNothing() throws Exception {
        db.seedIfEmpty();
        int loans = loanDao.findAll().size();
        int reservations = reservationDao.findAll().size();

        db.seedIfEmpty();

        assertEquals(loans, loanDao.findAll().size(), "a second seed should not add loans");
        assertEquals(reservations, reservationDao.findAll().size(), "a second seed should not add reservations");
    }
}
