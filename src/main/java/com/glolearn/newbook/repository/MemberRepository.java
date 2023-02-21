package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.domain.OAuthDomain;
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


    public void save(Member member) {em.persist(member);}

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public Member findByOAuthIdAndOAuthDomain(String oAuthId, OAuthDomain oAuthDomain) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QMember qMember = QMember.member;

        List<Member> members = query.select(qMember)
                .where(qMember.oAuthId.eq(oAuthId)
                        .and(qMember.oAuthDomain.eq(oAuthDomain))
                ).from(qMember).fetch();

        if(members.size() == 0){
            return null;
        }
        return members.get(0);
    }
}
