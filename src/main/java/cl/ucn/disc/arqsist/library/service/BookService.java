/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.model.Book;

import java.util.List;

/**
 * Catalog service: the single owner of the books and their available copy count.
 */
public final class BookService {

    /**
     * The DAO of the books table.
     */
    private final BookDao dao;

    /**
     * Creates the service.
     *
     * @param dao the DAO of the books table.
     */
    public BookService(BookDao dao) {
        this.dao = dao;
    }

    /**
     * Returns every book in the catalog.
     *
     * @return the list of books, empty when there are none.
     */
    public List<Book> listAll() {
        return dao.findAll();
    }

    /**
     * Returns the book with the given id.
     *
     * @param id the book id.
     * @return the book, or null when no book has that id.
     */
    public Book findById(int id) {
        return dao.findById(id);
    }

    /**
     * Adds a book to the catalog. Every copy starts as available.
     *
     * @param book the book to add.
     * @return the stored book, with its generated id.
     */
    public Book create(Book book) {
        book.setAvailableCopies(book.getTotalCopies());
        dao.create(book);
        return book;
    }

    /**
     * Takes one copy of a book out of the available stock.
     *
     * @param bookId the book id.
     * @throws NotFoundException     if no book has that id.
     * @throws IllegalStateException if the book has no available copies.
     */
    public void borrow(int bookId) {
        Book book = dao.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies of book " + bookId);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        dao.update(book);
    }

    /**
     * Puts one copy of a book back into the available stock.
     *
     * @param bookId the book id.
     * @throws NotFoundException if no book has that id.
     */
    public void returnCopy(int bookId) {
        Book book = dao.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        dao.update(book);
    }
}
