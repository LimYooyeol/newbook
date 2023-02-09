package com.glolearn.newbook.controller;

import com.glolearn.newbook.domain.Authorization;
import com.glolearn.newbook.exception.InvalidAccessTokenException;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.domain.OAuthDomain;
import com.glolearn.newbook.oauth.KakaoOAuthProvider;
import com.glolearn.newbook.oauth.OAuthProvider;
import com.glolearn.newbook.service.LoginService;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    private final KakaoOAuthProvider kakaoOAuthProvider;
    private final JwtUtils jwtUtils;

    // access token, refresh token 만료
    @GetMapping("/api/logout")
    public ResponseEntity<String> logout(HttpServletResponse httpResponse, @CookieValue(value = "refresh_token", required = false)String originalRefreshToken){
        loginService.removeAuthorization(originalRefreshToken);


        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .path("/").httpOnly(true).maxAge(0).sameSite("strict").build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .path("/").httpOnly(true).maxAge(0).sameSite("strict").build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return new ResponseEntity<>("/", HttpStatus.OK);
    }

    // access code 를 들고 도착하는 지점
    // 성공 시 본 서비스 이용을 위한 access token, refresh token 발행
    @GetMapping("/api/oauth/{provider}")
    public ResponseEntity<String> login(@RequestParam(name = "code") String accessCode, HttpServletResponse response,
                                            @PathVariable("provider") String provider){
        OAuthProvider oAuthProvider;
        OAuthDomain oAuthDomain;
        switch (provider){
            case "kakao" :
                oAuthProvider = kakaoOAuthProvider;
                oAuthDomain = OAuthDomain.KAKAO;
                break;
            default:
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String oAuthAccessToken;
        String oAuthId;

        // 1. OAuthId 얻기
        JSONObject httpBody = new JSONObject();
        try {
            oAuthAccessToken = oAuthProvider.getAccessToken(accessCode);
            oAuthId = oAuthProvider.getOAuthId(oAuthAccessToken);
        }catch(Exception e){
            httpBody.put("redirect_uri", "/login/error");
            httpBody.put("err_msg", e.getMessage());
            return new ResponseEntity(httpBody.toString(), HttpStatus.UNAUTHORIZED);
        }

        // 2. oAuthId 얻은 후 바로 access token 만료
        oAuthProvider.expireAccess(oAuthAccessToken);

        // 3. 회원조회 (새로운 회원이라면 추가)
        Member member = loginService.findOAuthMember(oAuthId, oAuthDomain);
        if(member == null){
            member = new Member(oAuthId, OAuthDomain.KAKAO, "닉네임" + oAuthId);
            loginService.addMember(member); // 동시 첫 로그인 확률 매우 낮음 + unique constraint(oauth_id, oauth_domain) 를 안걸어둬서 문제 없음.
        }

        // 4. JWT 생성
        String userAccessToken = jwtUtils.createAccessToken(member.getId());
        String userRefreshToken = loginService.addAuthorization(member.getId());

        // 5. 응답
        httpBody.put("redirect_uri", "/");
        HttpHeaders headers = new HttpHeaders();

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", userAccessToken)
                .path("/").httpOnly(true).maxAge(jwtUtils.ACCESS_TOKEN_LIFESPAN_SEC)
                .sameSite("strict").build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", userRefreshToken)
                .path("/").httpOnly(true).maxAge(jwtUtils.REFRESH_TOKEN_LIFESPAN_SEC)
                .sameSite("strict").build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());


        ResponseEntity responseEntity = new ResponseEntity(httpBody.toString(), headers, HttpStatus.OK);
        return responseEntity;
    }


}
