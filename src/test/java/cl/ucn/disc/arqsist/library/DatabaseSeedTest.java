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

class DatabaseSeedTest {

    private Database db;
    private BookDao bookDao;
    private LoanDao loanDao;
    private ReservationDao reservationDao;

    @BeforeEach
    void setUp() throws Exception {
        db = new Database("jdbc:sqlite::memory:");
        bookDao = new BookDao(db.connectionSource());
        loanDao = new LoanDao(db.connectionSource());
        reservationDao = new ReservationDao(db.connectionSource());
    }

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
