/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Loan service: lists the loans, returns a loan, and finds the overdue loans.
 */
public final class LoanService {

    /**
     * The original loan period. No code reads it: {@link LoanPolicy#DUE_DAYS} holds the rule. Stage 2 deletes it.
     */
    public static final int DUE_DAYS = 21;

    /**
     * The DAO of the loans table.
     */
    private final LoanDao loanDao;

    /**
     * The DAO of the books table, used to put a returned copy back in stock.
     */
    private final BookDao bookDao;

    /**
     * Creates the service.
     *
     * @param loanDao the DAO of the loans table.
     * @param bookDao the DAO of the books table.
     */
    public LoanService(LoanDao loanDao, BookDao bookDao) {
        this.loanDao = loanDao;
        this.bookDao = bookDao;
    }

    /**
     * Returns every loan.
     *
     * @return the list of loans, empty when there are none.
     * @throws SQLException declared by the original signature; the current DAOs report failures as
     *                      RuntimeException.
     */
    public List<Loan> findAll() throws SQLException {
        return loanDao.findAll();
    }

    /**
     * Marks a loan as returned, charges the overdue fee when the loan is late, and puts the copy back in stock.
     *
     * @param loanId the loan id.
     * @return the returned loan, or the loan unchanged when it is missing (null) or already returned.
     * @throws SQLException declared by the original signature; the current DAOs report failures as
     *                      RuntimeException.
     */
    public Loan returnLoan(int loanId) throws SQLException {
        Loan loan = loanDao.findById(loanId);
        if (loan == null || loan.isReturned()) {
            return loan;
        }

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());

        LocalDate due = loan.getDueDate();
        LocalDate today = LocalDate.now();
        if (today.isAfter(due)) {
            long daysOverdue = ChronoUnit.DAYS.between(due, today);
            loan.setOverdueFee(daysOverdue * 1.0);
        }

        loanDao.update(loan);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookDao.update(book);

        return loan;
    }

    /**
     * Returns the open loans whose due date is before today.
     *
     * @return the list of overdue loans, empty when there are none.
     * @throws SQLException declared by the original signature; the current DAOs report failures as
     *                      RuntimeException.
     */
    public List<Loan> overdueLoans() throws SQLException {
        LocalDate today = LocalDate.now();
        return loanDao.findAll().stream()
                .filter(loan -> !loan.isReturned() && loan.getDueDate().isBefore(today))
                .toList();
    }
}
