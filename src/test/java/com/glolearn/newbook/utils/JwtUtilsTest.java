package com.glolearn.newbook.utils;

import com.glolearn.newbook.exception.InvalidJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilsTest {
    @Autowired JwtUtils jwtUtils;

    @Test
    public void AT_생성_조회(){
        //given
        Long memberId = 1L;

        //when
        String accessToken = jwtUtils.createAccessToken(memberId);

        //then
        assertEquals(memberId, jwtUtils.getSub(accessToken));
    }

    @Test
    public void AT_조회_예외(){
        //given
        Long memberId = 1L;

        //when
        String accessToken = jwtUtils.createAccessToken(memberId);
        String[] parser = accessToken.split("\\.");
        String modified = parser[0] + "." + (parser[1].charAt(0) + 1) + parser[1].substring(1) + "." + parser[2];

        //then
        assertThrows(InvalidJwtException.class, () -> jwtUtils.getSub(modified));
    }

}