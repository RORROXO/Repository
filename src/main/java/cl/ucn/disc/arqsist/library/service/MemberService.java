/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.model.Member;

import java.util.List;

/**
 * Member service: registers and lists members.
 */
public final class MemberService {

    /**
     * The DAO of the members table.
     */
    private final MemberDao memberDao;

    /**
     * Creates the service.
     *
     * @param memberDao the DAO of the members table.
     */
    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    /**
     * Registers a new member.
     *
     * @param member the member to register.
     * @return the stored member, with its generated id.
     */
    public Member register(Member member) {
        memberDao.create(member);
        return member;
    }

    /**
     * Returns every member.
     *
     * @return the list of members, empty when there are none.
     */
    public List<Member> findAll() {
        return memberDao.findAll();
    }
}
