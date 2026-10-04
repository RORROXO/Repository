/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.model;

import cl.ucn.disc.arqsist.library.db.LocalDatePersister;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDate;

/**
 * A reservation of a book by a member. Fulfilling the reservation turns it into a loan.
 */
@DatabaseTable(tableName = "reservations")
public final class Reservation {

    /**
     * The generated id.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The member who reserves the book.
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Member member;

    /**
     * The reserved book.
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Book book;

    /**
     * The date the reservation was made.
     */
    @DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
    private LocalDate reservedAt;

    /**
     * True when the reservation was turned into a loan.
     */
    @DatabaseField
    private boolean fulfilled;

    /**
     * Creates an empty reservation. ORMLite and Jackson need this constructor.
     */
    public Reservation() {
    }

    /**
     * Creates a pending reservation.
     *
     * @param member     the member who reserves the book.
     * @param book       the reserved book.
     * @param reservedAt the date the reservation is made.
     */
    public Reservation(Member member, Book book, LocalDate reservedAt) {
        this.member = member;
        this.book = book;
        this.reservedAt = reservedAt;
        this.fulfilled = false;
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
     * @return the member who reserves the book.
     */
    public Member getMember() {
        return member;
    }

    /**
     * Sets the member.
     *
     * @param member the member who reserves the book.
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * Returns the book.
     *
     * @return the reserved book.
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the book.
     *
     * @param book the reserved book.
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Returns the reservation date.
     *
     * @return the date the reservation was made.
     */
    public LocalDate getReservedAt() {
        return reservedAt;
    }

    /**
     * Sets the reservation date.
     *
     * @param reservedAt the date the reservation was made.
     */
    public void setReservedAt(LocalDate reservedAt) {
        this.reservedAt = reservedAt;
    }

    /**
     * Tells whether the reservation was fulfilled.
     *
     * @return true when the reservation was turned into a loan.
     */
    public boolean isFulfilled() {
        return fulfilled;
    }

    /**
     * Sets the fulfilled flag.
     *
     * @param fulfilled true when the reservation was turned into a loan.
     */
    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }
}
