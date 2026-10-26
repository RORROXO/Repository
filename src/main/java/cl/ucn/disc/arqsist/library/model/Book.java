/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A title in the catalog, with its total and available copies.
 */
@DatabaseTable(tableName = "books")
public final class Book {

    /**
     * The generated id.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The title.
     */
    @DatabaseField(canBeNull = false)
    private String title;

    /**
     * The author.
     */
    @DatabaseField(canBeNull = false)
    private String author;

    /**
     * The ISBN.
     */
    @DatabaseField(canBeNull = false)
    private String isbn;

    /**
     * The number of copies the library owns.
     */
    @DatabaseField(canBeNull = false)
    private int totalCopies;

    /**
     * The number of copies on the shelf, not on loan.
     */
    @DatabaseField(canBeNull = false)
    private int availableCopies;

    /**
     * Creates an empty book. ORMLite and Jackson need this constructor.
     */
    public Book() {
    }

    /**
     * Creates a book with every copy available.
     *
     * @param title       the title.
     * @param author      the author.
     * @param isbn        the ISBN.
     * @param totalCopies the number of copies the library owns.
     */
    public Book(String title, String author, String isbn, int totalCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
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
     * Returns the title.
     *
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the author.
     *
     * @return the author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author.
     *
     * @param author the author.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the ISBN.
     *
     * @return the ISBN.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN.
     *
     * @param isbn the ISBN.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Returns the number of copies the library owns.
     *
     * @return the total copies.
     */
    public int getTotalCopies() {
        return totalCopies;
    }

    /**
     * Sets the number of copies the library owns.
     *
     * @param totalCopies the total copies.
     */
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    /**
     * Returns the number of copies on the shelf.
     *
     * @return the available copies.
     */
    public int getAvailableCopies() {
        return availableCopies;
    }

    /**
     * Sets the number of copies on the shelf.
     *
     * @param availableCopies the available copies.
     */
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}
