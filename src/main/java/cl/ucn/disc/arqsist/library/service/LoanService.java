/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Loan service: lends a book to a member, lists the loans, returns a loan, and finds the overdue loans.
 */
public final class LoanService {

    /**
     * The DAO of the loans table.
     */
    private final LoanDao loanDao;

    /**
     * The DAO of the members table, used to check the borrower.
     */
    private final MemberDao memberDao;

    /**
     * The catalog service, the only owner of the copy count.
     */
    private final BookService bookService;

    /**
     * The clock that gives "today".
     */
    private final Clock clock;

    /**
     * Creates the service.
     *
     * @param loanDao     the DAO of the loans table.
     * @param memberDao   the DAO of the members table.
     * @param bookService the catalog service.
     * @param clock       the clock that gives "today".
     */
    public LoanService(LoanDao loanDao, MemberDao memberDao, BookService bookService, Clock clock) {
        this.loanDao = loanDao;
        this.memberDao = memberDao;
        this.bookService = bookService;
        this.clock = clock;
    }

    /**
     * Lends one copy of a book to a member. The copy decrement and the new loan run in one transaction.
     *
     * @param memberId the member id.
     * @param bookId   the book id.
     * @return the new loan, due {@link LoanPolicy#DUE_DAYS} days from today.
     * @throws NotFoundException     if no member or no book has that id.
     * @throws IllegalStateException if the book has no available copies.
     */
    public Loan checkout(int memberId, int bookId) {
        Member member = memberDao.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found: " + memberId);
        }
        if (bookService.findById(bookId) == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }

        LocalDate today = LocalDate.now(clock);
        return inTransaction(() -> {
            bookService.borrow(bookId);
            Book book = bookService.findById(bookId);
            Loan loan = new Loan(member, book, today, LoanPolicy.dueDate(today));
            loanDao.create(loan);
            return loan;
        });
    }

    /**
     * Returns every loan. Each open overdue loan shows the fee accrued up to today.
     *
     * @return the list of loans, empty when there are none.
     */
    public List<Loan> findAll() {
        return withAccruedFees(loanDao.findAll());
    }

    /**
     * Marks a loan as returned, charges the overdue fee when the loan is late, and puts the copy back in stock.
     * The loan update and the copy increment run in one transaction.
     *
     * @param loanId the loan id.
     * @return the returned loan.
     * @throws NotFoundException     if no loan has that id.
     * @throws IllegalStateException if the loan is already returned.
     */
    public Loan returnLoan(int loanId) {
        Loan loan = loanDao.findById(loanId);
        if (loan == null) {
            throw new NotFoundException("Loan not found: " + loanId);
        }
        if (loan.isReturned()) {
            throw new IllegalStateException("Loan already returned: " + loanId);
        }

        LocalDate today = LocalDate.now(clock);
        return inTransaction(() -> {
            loan.setReturned(true);
            loan.setReturnDate(today);
            if (today.isAfter(loan.getDueDate())) {
                loan.setOverdueFee(accruedFee(loan, today));
            }
            loanDao.update(loan);

            int bookId = loan.getBook().getId();
            bookService.returnCopy(bookId);
            loan.setBook(bookService.findById(bookId));
            return loan;
        });
    }

    /**
     * Returns the open loans whose due date is before today, each with the fee accrued up to today.
     *
     * @return the list of overdue loans, empty when there are none.
     */
    public List<Loan> overdueLoans() {
        LocalDate today = LocalDate.now(clock);
        return withAccruedFees(loanDao.findAll().stream()
                .filter(loan -> !loan.isReturned() && loan.getDueDate().isBefore(today))
                .toList());
    }

    /**
     * Sets the fee accrued up to today on every open overdue loan. The fee is not persisted: it is only stored
     * when the loan is returned.
     *
     * @param loans the loans to update.
     * @return the same list of loans.
     */
    private List<Loan> withAccruedFees(List<Loan> loans) {
        LocalDate today = LocalDate.now(clock);
        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.getDueDate().isBefore(today)) {
                loan.setOverdueFee(accruedFee(loan, today));
            }
        }
        return loans;
    }

    /**
     * Computes the overdue fee of a loan: {@link LoanPolicy#FEE_PER_DAY} for each day past the due date.
     *
     * @param loan  the loan.
     * @param today the current date.
     * @return the fee accrued up to today.
     */
    private static double accruedFee(Loan loan, LocalDate today) {
        return ChronoUnit.DAYS.between(loan.getDueDate(), today) * LoanPolicy.FEE_PER_DAY;
    }

    /**
     * Runs the given work in one database transaction. A domain error thrown by the work keeps its type.
     *
     * @param work the work to run.
     * @param <R>  the type of the result.
     * @return the result of the work.
     * @throws RuntimeException if the database fails; it wraps the {@link SQLException}.
     */
    private <R> R inTransaction(Callable<R> work) {
        try {
            return loanDao.transaction(work);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
