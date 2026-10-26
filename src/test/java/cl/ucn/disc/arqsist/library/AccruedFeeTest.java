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

class AccruedFeeTest {

    private LoanService loanService;
    private Loan overdue;
    private Loan active;

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

    @Test
    void findAllShowsTheFeeAccruedByOpenOverdueLoans() {
        List<Loan> loans = loanService.findAll();

        assertEquals(24.0, feeOf(loans, overdue.getId()), "24 days past 2025-12-22 at 1.0 per day");
        assertEquals(0.0, feeOf(loans, active.getId()), "a loan that is not due yet has no fee");
    }

    private static double feeOf(List<Loan> loans, int loanId) {
        return loans.stream()
                .filter(loan -> loan.getId() == loanId)
                .findFirst()
                .orElseThrow()
                .getOverdueFee();
    }
}
