package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Auth.AuthInfo;
import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.utils.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class AuthInfoServiceTest {

    @Autowired AuthInfoService authInfoService;
    @Autowired MemberService memberService;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    EntityManager em;

    @Test
    public void 인증추가_조회_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.NAVER, "홍길동");
        memberService.addMember(member);
        String refreshToken = jwtUtils.createRefreshToken();

        //when
        authInfoService.addAuthInfo(refreshToken, member.getId());
        AuthInfo authInfo = authInfoService.findAuthInfo(refreshToken);

        //then
        assertNotNull(authInfo);
        assertTrue(member == authInfo.getMember());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대한 인증 추가는 불가능")
    public void 인증추가_예외_테스트(){
        //given

        //when
        authInfoService.addAuthInfo(jwtUtils.createRefreshToken(), -1L);

        //then
        assertThrows(PersistenceException.class,
                ()-> em.flush());

    }

    @Test
    public void 인증제거_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.NAVER, "홍길동");
        memberService.addMember(member);
        String refreshToken = jwtUtils.createRefreshToken();
        authInfoService.addAuthInfo(refreshToken, member.getId());

        //when
        authInfoService.removeAuthInfo(refreshToken);
        AuthInfo authInfo = authInfoService.findAuthInfo(refreshToken);

        //then
        assertNull(authInfo);
    }

    @Test
    public void 인증제거_예외_테스트(){
        //given
        String refreshToken = jwtUtils.createRefreshToken();

        //when, then
        assertThrows(EmptyResultDataAccessException.class,
                () -> authInfoService.removeAuthInfo(refreshToken));
    }
}