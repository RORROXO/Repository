/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.model;

import cl.ucn.disc.arqsist.library.db.LocalDatePersister;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDate;

/**
 * A loan of one copy of a book to a member.
 */
@DatabaseTable(tableName = "loans")
public final class Loan {

    /**
     * The generated id.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The member who borrows the book.
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Member member;

    /**
     * The borrowed book.
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Book book;

    /**
     * The date the loan starts.
     */
    @DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
    private LocalDate loanDate;

    /**
     * The date the book must be returned.
     */
    @DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
    private LocalDate dueDate;

    /**
     * The date the book was returned, or null while the loan is open.
     */
    @DatabaseField(persisterClass = LocalDatePersister.class)
    private LocalDate returnDate;

    /**
     * True when the book was returned.
     */
    @DatabaseField
    private boolean returned;

    /**
     * The fee charged for the days past the due date.
     */
    @DatabaseField
    private double overdueFee;

    /**
     * Creates an empty loan. ORMLite and Jackson need this constructor.
     */
    public Loan() {
    }

    /**
     * Creates an open loan with no fee.
     *
     * @param member   the member who borrows the book.
     * @param book     the borrowed book.
     * @param loanDate the date the loan starts.
     * @param dueDate  the date the book must be returned.
     */
    public Loan(Member member, Book book, LocalDate loanDate, LocalDate dueDate) {
        this.member = member;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = false;
        this.overdueFee = 0.0;
    }

    /**
     * Returns the id.
     *
     * @return the generated id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the member.
     *
     * @return the member who borrows the book.
     */
    public Member getMember() {
        return member;
    }

    /**
     * Sets the member.
     *
     * @param member the member who borrows the book.
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * Returns the book.
     *
     * @return the borrowed book.
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the book.
     *
     * @param book the borrowed book.
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Returns the loan date.
     *
     * @return the date the loan starts.
     */
    public LocalDate getLoanDate() {
        return loanDate;
    }

    /**
     * Sets the loan date.
     *
     * @param loanDate the date the loan starts.
     */
    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Returns the due date.
     *
     * @return the date the book must be returned.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the date the book must be returned.
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Returns the return date.
     *
     * @return the date the book was returned, or null while the loan is open.
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date.
     *
     * @param returnDate the date the book was returned.
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Tells whether the book was returned.
     *
     * @return true when the book was returned.
     */
    public boolean isReturned() {
        return returned;
    }

    /**
     * Sets the returned flag.
     *
     * @param returned true when the book was returned.
     */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Returns the overdue fee.
     *
     * @return the fee charged for the days past the due date.
     */
    public double getOverdueFee() {
        return overdueFee;
    }

    /**
     * Sets the overdue fee.
     *
     * @param overdueFee the fee charged for the days past the due date.
     */
    public void setOverdueFee(double overdueFee) {
        this.overdueFee = overdueFee;
    }
}
