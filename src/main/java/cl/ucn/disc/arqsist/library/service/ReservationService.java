/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Reservation service: reserves books, lists the reservations, and turns a reservation into a loan.
 */
public final class ReservationService {

    /**
     * The DAO of the reservations table.
     */
    private final ReservationDao reservationDao;

    /**
     * The DAO of the books table.
     */
    private final BookDao bookDao;

    /**
     * The DAO of the members table.
     */
    private final MemberDao memberDao;

    /**
     * The DAO of the loans table.
     */
    private final LoanDao loanDao;

    /**
     * Creates the service.
     *
     * @param reservationDao the DAO of the reservations table.
     * @param bookDao        the DAO of the books table.
     * @param memberDao      the DAO of the members table.
     * @param loanDao        the DAO of the loans table.
     */
    public ReservationService(ReservationDao reservationDao, BookDao bookDao, MemberDao memberDao, LoanDao loanDao) {
        this.reservationDao = reservationDao;
        this.bookDao = bookDao;
        this.memberDao = memberDao;
        this.loanDao = loanDao;
    }

    /**
     * Reserves a book for a member, dated today.
     *
     * @param bookId   the book id.
     * @param memberId the member id.
     * @return the new reservation.
     * @throws SQLException declared by the original signature; the current DAOs report failures as
     *                      RuntimeException.
     */
    public Reservation reserve(int bookId, int memberId) throws SQLException {
        Book book = bookDao.findById(bookId);
        Member member = memberDao.findById(memberId);
        Reservation reservation = new Reservation(member, book, LocalDate.now());
        reservationDao.create(reservation);
        return reservation;
    }

    /**
     * Returns every reservation.
     *
     * @return the list of reservations, empty when there are none.
     * @throws SQLException declared by the original signature; the current DAOs report failures as
     *                      RuntimeException.
     */
    public List<Reservation> findAll() throws SQLException {
        return reservationDao.findAll();
    }

    /**
     * Turns a pending reservation into a loan. The loan is due {@link LoanPolicy#DUE_DAYS} days from today.
     *
     * @param reservationId the reservation id.
     * @return the new loan.
     * @throws IllegalStateException if the reservation is missing or already fulfilled.
     * @throws SQLException          declared by the original signature; the current DAOs report failures as
     *                               RuntimeException.
     */
    public Loan fulfill(int reservationId) throws SQLException {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null || reservation.isFulfilled()) {
            throw new IllegalStateException("Reservation not available");
        }

        reservation.setFulfilled(true);
        reservationDao.update(reservation);

        LocalDate dueDate = LoanPolicy.dueDate(LocalDate.now());
        Loan loan = new Loan(reservation.getMember(), reservation.getBook(), LocalDate.now(), dueDate);
        loanDao.create(loan);
        return loan;
    }
}
