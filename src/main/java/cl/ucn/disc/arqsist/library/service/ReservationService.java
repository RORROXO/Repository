/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Reservation service: reserves books, lists the reservations, and turns a reservation into a loan.
 */
public final class ReservationService {

    /**
     * The DAO of the reservations table.
     */
    private final ReservationDao reservationDao;

    /**
     * The DAO of the members table.
     */
    private final MemberDao memberDao;

    /**
     * The DAO of the loans table.
     */
    private final LoanDao loanDao;

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
     * @param reservationDao the DAO of the reservations table.
     * @param memberDao      the DAO of the members table.
     * @param loanDao        the DAO of the loans table.
     * @param bookService    the catalog service.
     * @param clock          the clock that gives "today".
     */
    public ReservationService(ReservationDao reservationDao, MemberDao memberDao, LoanDao loanDao,
                              BookService bookService, Clock clock) {
        this.reservationDao = reservationDao;
        this.memberDao = memberDao;
        this.loanDao = loanDao;
        this.bookService = bookService;
        this.clock = clock;
    }

    /**
     * Reserves a book for a member, dated today.
     *
     * @param bookId   the book id.
     * @param memberId the member id.
     * @return the new reservation.
     * @throws NotFoundException if no book or no member has that id.
     */
    public Reservation reserve(int bookId, int memberId) {
        Book book = bookService.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        Member member = memberDao.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found: " + memberId);
        }

        Reservation reservation = new Reservation(member, book, LocalDate.now(clock));
        reservationDao.create(reservation);
        return reservation;
    }

    /**
     * Returns every reservation.
     *
     * @return the list of reservations, empty when there are none.
     */
    public List<Reservation> findAll() {
        return reservationDao.findAll();
    }

    /**
     * Turns a pending reservation into a loan. The reservation update, the copy decrement, and the new loan run
     * in one transaction.
     *
     * @param reservationId the reservation id.
     * @return the new loan, due {@link LoanPolicy#DUE_DAYS} days from today.
     * @throws NotFoundException     if no reservation has that id.
     * @throws IllegalStateException if the reservation is already fulfilled, or the book has no available copies.
     */
    public Loan fulfill(int reservationId) {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new NotFoundException("Reservation not found: " + reservationId);
        }
        if (reservation.isFulfilled()) {
            throw new IllegalStateException("Reservation already fulfilled: " + reservationId);
        }

        LocalDate today = LocalDate.now(clock);
        return inTransaction(() -> {
            reservation.setFulfilled(true);
            reservationDao.update(reservation);

            int bookId = reservation.getBook().getId();
            bookService.borrow(bookId);
            Book book = bookService.findById(bookId);
            Loan loan = new Loan(reservation.getMember(), book, today, LoanPolicy.dueDate(today));
            loanDao.create(loan);
            return loan;
        });
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
            return reservationDao.transaction(work);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
