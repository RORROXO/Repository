/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Member service: registers and lists members. It also runs the checkout until Stage 2 moves it to LoanService.
 */
public final class MemberService {

    /**
     * The DAO of the members table.
     */
    private final MemberDao memberDao;

    /**
     * The DAO of the books table, used by the checkout.
     */
    private final BookDao bookDao;

    /**
     * The DAO of the loans table, used by the checkout.
     */
    private final LoanDao loanDao;

    /**
     * Creates the service.
     *
     * @param memberDao the DAO of the members table.
     * @param bookDao   the DAO of the books table.
     * @param loanDao   the DAO of the loans table.
     */
    public MemberService(MemberDao memberDao, BookDao bookDao, LoanDao loanDao) {
        this.memberDao = memberDao;
        this.bookDao = bookDao;
        this.loanDao = loanDao;
    }

    /**
     * Registers a new member.
     *
     * @param member the member to register.
     * @return the stored member, with its generated id.
     * @throws SQLException declared by the original signature; the current DAOs report failures as
     *                      RuntimeException.
     */
    public Member register(Member member) throws SQLException {
        memberDao.create(member);
        return member;
    }

    /**
     * Returns every member.
     *
     * @return the list of members, empty when there are none.
     * @throws SQLException declared by the original signature; the current DAOs report failures as
     *                      RuntimeException.
     */
    public List<Member> findAll() throws SQLException {
        return memberDao.findAll();
    }

    /**
     * Lends one copy of a book to a member. The loan is due {@link LoanPolicy#DUE_DAYS} days from today.
     *
     * @param memberId the member id.
     * @param bookId   the book id.
     * @return the new loan.
     * @throws SQLException declared by the original signature; the current DAOs report failures as
     *                      RuntimeException.
     */
    public Loan checkout(int memberId, int bookId) throws SQLException {
        Member member = memberDao.findById(memberId);
        Book book = bookDao.findById(bookId);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDao.update(book);

        LocalDate dueDate = LoanPolicy.dueDate(LocalDate.now());
        Loan loan = new Loan(member, book, LocalDate.now(), dueDate);
        loanDao.create(loan);
        return loan;
    }
}
