package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Auth.AuthInfo;
import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class AuthInfoServiceTest {

    @Autowired AuthInfoService authInfoService;
    @Autowired MemberService memberService;
    @Autowired
    JwtUtils jwtUtils;

    @Test
    public void 인증추가_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.NAVER, "홍길동");
        memberService.addMember(member);
        String refreshToken = jwtUtils.createRefreshToken();

        //when
        authInfoService.addAuthInfo(refreshToken, member);
        AuthInfo authInfo = authInfoService.findAuthInfo(refreshToken);

        //then
        assertNotNull(authInfo);
        assertTrue(member == authInfo.getMember());
    }

    @Test
    public void 인증제거_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.NAVER, "홍길동");
        memberService.addMember(member);
        String refreshToken = jwtUtils.createRefreshToken();
        authInfoService.addAuthInfo(refreshToken, member);

        //when
        authInfoService.removeAuthInfo(refreshToken);
        AuthInfo authInfo = authInfoService.findAuthInfo(refreshToken);

        //then
        assertNull(authInfo);
    }
}