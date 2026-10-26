/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.BookService;
import cl.ucn.disc.arqsist.library.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Detects Error #11: the loan list must show the fee accrued by each open overdue loan.
 */
class AccruedFeeTest {

    /**
     * The loan service under test.
     */
    private LoanService loanService;

    /**
     * An open loan due on 2025-12-22, 24 days before the fixed clock.
     */
    private Loan overdue;

    /**
     * An open loan that is not due yet.
     */
    private Loan active;

    /**
     * Creates an in-memory database, the loan service with a fixed clock on 2026-01-15, a book, a member, and the
     * two loans.
     *
     * @throws Exception if the database cannot be created.
     */
    @BeforeEach
    void setUp() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        BookDao bookDao = new BookDao(db.connectionSource());
        MemberDao memberDao = new MemberDao(db.connectionSource());
        LoanDao loanDao = new LoanDao(db.connectionSource());

        Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
        loanService = new LoanService(loanDao, memberDao, new BookService(bookDao), clock);

        Book book = new Book("Refactoring", "Martin Fowler", "9780134757599", 2);
        bookDao.create(book);
        Member member = new Member("Barbara Liskov", "barbara@example.com");
        memberDao.create(member);

        overdue = new Loan(member, book, LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 22));
        loanDao.create(overdue);
        active = new Loan(member, book, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 31));
        loanDao.create(active);
    }

    /**
     * The overdue loan shows 24.0 and the active loan shows 0.0.
     */
    @Test
    void findAllShowsTheFeeAccruedByOpenOverdueLoans() {
        List<Loan> loans = loanService.findAll();

        assertEquals(24.0, feeOf(loans, overdue.getId()), "24 days past 2025-12-22 at 1.0 per day");
        assertEquals(0.0, feeOf(loans, active.getId()), "a loan that is not due yet has no fee");
    }

    /**
     * Returns the overdue fee of one loan in a list.
     *
     * @param loans  the list of loans.
     * @param loanId the id of the loan.
     * @return the overdue fee of that loan.
     * @throws java.util.NoSuchElementException if no loan in the list has that id.
     */
    private static double feeOf(List<Loan> loans, int loanId) {
        return loans.stream()
                .filter(loan -> loan.getId() == loanId)
                .findFirst()
                .orElseThrow()
                .getOverdueFee();
    }
}
