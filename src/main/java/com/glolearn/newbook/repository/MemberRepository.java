package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;


    // 회원 추가
    public void save(Member member) {
        em.persist(member);
    }

    // 회원 조회(ID)
    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    // 회원 조회(OAuth Id && OAuth Domain)
    public Member findByOauthIdAndOauthDomain(String oauthId, OauthDomain oauthDomain) {

        String query = "SELECT m FROM Member m " +
                        " WHERE m.oauthId =:oauthId " +
                        " AND " +
                        " m.oauthDomain =: oauthDomain";

        Member findMember = (Member) em.createQuery(query)
                .setParameter("oauthId", oauthId)
                .setParameter("oauthDomain", oauthDomain)
                .getResultList().stream().findFirst().orElse(null);

        return findMember;
    }
}
