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


    public Long save(Member member) {
        if(member.getId() == null){
            em.persist(member);
        }else{
            em.merge(member);
        }
        return member.getId();
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public Member findByOauthIdAndOauthDomain(String oauthId, OauthDomain oauthDomain) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QMember qMember = QMember.member;

        List<Member> members = query.select(qMember)
                .where(qMember.oauthId.eq(oauthId)
                        .and(qMember.oauthDomain.eq(oauthDomain))
                ).from(qMember).fetch();

        if(members.size() == 0){
            return null;
        }
        return members.get(0);
    }
}
