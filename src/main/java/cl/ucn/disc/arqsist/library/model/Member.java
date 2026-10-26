/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A registered member who can borrow and reserve books.
 */
@DatabaseTable(tableName = "members")
public final class Member {

    /**
     * The generated id.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The full name.
     */
    @DatabaseField(canBeNull = false)
    private String name;

    /**
     * The email address.
     */
    @DatabaseField(canBeNull = false)
    private String email;

    /**
     * Creates an empty member. ORMLite and Jackson need this constructor.
     */
    public Member() {
    }

    /**
     * Creates a member.
     *
     * @param name  the full name.
     * @param email the email address.
     */
    public Member(String name, String email) {
        this.name = name;
        this.email = email;
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
     * Returns the full name.
     *
     * @return the full name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the full name.
     *
     * @param name the full name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the email address.
     *
     * @return the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
