package com.glolearn.newbook.oauth;

import com.glolearn.newbook.oauth.exception.AccessTokenIssueRejectedException;
import com.glolearn.newbook.oauth.exception.InvalidAccessTokenException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class KakaoOauthProviderTest{

    @Test
    public void 토큰발행_실패_테스트(){
        //given
        KakaoOauthProvider kakaoOauthProvider = new KakaoOauthProvider();

        //when, then
        assertThrows(AccessTokenIssueRejectedException.class, () -> kakaoOauthProvider.getAccessToken("1234"));
    }

    @Test
    public void ID획득_실패_테스트(){
        //given
        KakaoOauthProvider kakaoOauthProvider = new KakaoOauthProvider();

        //when, then
        assertThrows(InvalidAccessTokenException.class, () -> kakaoOauthProvider.getOAuthId("1234"));
    }

    @Test
    public void 임의토큰_만료시_예외없음(){
        //given
        KakaoOauthProvider kakaoOauthProvider = new KakaoOauthProvider();

        //when, then
        assertDoesNotThrow(() -> kakaoOauthProvider.expireAccess("1234"));
    }
}