/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import java.time.LocalDate;

/**
 * The single source of the lending rules: the loan period and the overdue fee rate.
 */
public final class LoanPolicy {

    /**
     * The number of days a loan lasts before it becomes overdue.
     */
    public static final int DUE_DAYS = 21;

    /**
     * The fee charged for each day a loan is past its due date.
     */
    public static final double FEE_PER_DAY = 1.0;

    /**
     * Prevents instantiation: this class only holds constants and static methods.
     */
    private LoanPolicy() {
    }

    /**
     * Computes the due date of a loan.
     *
     * @param loanDate the date the loan starts.
     * @return the date the loan is due, {@link #DUE_DAYS} days after the loan date.
     */
    public static LocalDate dueDate(LocalDate loanDate) {
        return loanDate.plusDays(DUE_DAYS);
    }
}
