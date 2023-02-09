package com.glolearn.newbook.utils;

import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilsTest {

    @Autowired
    JwtUtils jwtUtils;

    @Test
    public void 인증_성공_테스트(){
        // given
        Long memberId = 3L;

        // when
        String accessToken = jwtUtils.createAccessToken(memberId);

        // then
        System.out.println(accessToken);
    }

    @Test
    public void 사용자_인증_테스트(){
        //given
        Long memberId = 3L;

        //when
        String accessToken = jwtUtils.createAccessToken(memberId);
        long sub = jwtUtils.getSub(accessToken);

        //then
        assertEquals(sub, memberId);
    }

    @Test
    public void 인증_실패_테스트_위조(){
        // given
        String accessToken = jwtUtils.createAccessToken(3L);

        // when
        int idx = accessToken.indexOf('.') + 1;
        int newChar = accessToken.charAt(idx) + 1;
        String modifiedToken = accessToken.substring(0, idx + 1) + (char)newChar + accessToken.substring(idx + 2);

        // then
        assertThrows(SignatureException.class, () -> jwtUtils.getSub(modifiedToken));

    }
}