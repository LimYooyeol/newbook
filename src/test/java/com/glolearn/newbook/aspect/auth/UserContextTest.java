package com.glolearn.newbook.aspect.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserContextTest {

    @Test
    public void 유저설정_테스트(){
        //given, when
        UserContext.setCurrentMember(1L);

        //then
        assertEquals(1L, UserContext.getCurrentMember());
    }

    @Test
    public void 초기화_테스트(){
        //given
        UserContext.setCurrentMember(1L);
        UserContext.setReissueFlag();

        //when
        UserContext.clear();

        //then
        assertNull(UserContext.getCurrentMember());
        assertFalse(UserContext.getReissueFlag());
    }

}