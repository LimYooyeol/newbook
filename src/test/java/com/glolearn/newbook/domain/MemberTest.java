package com.glolearn.newbook.domain;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
public class MemberTest {

    @Autowired
    EntityManager em;

    public static Member createTestMember(){
        return Member.createMember("testOAuthId", OauthDomain.KAKAO, "테스트 닉네임");
    }

    @Test
    public void 검증_테스트(){
        Member member = Member.createMember("temp", OauthDomain.KAKAO, "10글자가 넘는 길이의 이름");

        em.persist(member);

        em.flush();
    }

}