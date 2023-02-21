package com.glolearn.newbook.controller;

import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.domain.OAuthDomain;
import com.glolearn.newbook.oauth.KakaoOAuthProvider;
import com.glolearn.newbook.oauth.OAuthProvider;
import com.glolearn.newbook.service.AuthService;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private final KakaoOAuthProvider kakaoOAuthProvider;

    private final JwtUtils jwtUtils;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    /*
        #shorts:
            로그아웃 (token 만료 + DB refresh token 제거)
     */
    @DeleteMapping("/oauth/token")
    public String logout(
            HttpServletResponse response,
            @CookieValue(value = "refresh_token", required = false)
            String originalRefreshToken
    ){
        // DB에서 refersh token 제거
        authService.removeAuthorization(originalRefreshToken);

        // access token 과 refresh token 을 모두 만료 처리
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .path("/").httpOnly(true).maxAge(0).sameSite("strict").build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .path("/").httpOnly(true).maxAge(0).sameSite("strict").build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return "redirect:/";
    }


    /*
        #shorts :
            OAuth 인증 시 access code 를 들고 redirect 되는 엔드 포인트.
            글로런 서비스 이용을 위한 access token 및 refresh token 발급
        #to do  :
            인증 실패 경우 시 예외처리 세분화
     */
    @GetMapping("/oauth/{provider}/token")
    public String oauth(
            @PathVariable("provider") String provider,
            @RequestParam(name = "code") String accessCode,
            @RequestParam(name = "state", defaultValue = "/") String redirectPath,
            HttpServletResponse response
    ){
        OAuthProvider oAuthProvider;
        OAuthDomain oAuthDomain;

        switch (provider){
            case "kakao" :
                oAuthProvider = kakaoOAuthProvider;
                oAuthDomain = OAuthDomain.KAKAO;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        String oAuthAccessToken;
        String oAuthId;

        // 1. OAuthId 얻기
        JSONObject httpBody = new JSONObject();
        try {
            oAuthAccessToken = oAuthProvider.getAccessToken(accessCode);
            oAuthId = oAuthProvider.getOAuthId(oAuthAccessToken);
        }catch(Exception e){   // resource server 로부터 resource owner 정보를 불러오는 것에 실패한 경우
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // 2. oAuthId 얻은 후 바로 (oauth domain 접근 위한) access token 만료
        oAuthProvider.expireAccess(oAuthAccessToken);

        // 3. 회원조회 (새로운 회원이라면 추가)
        Member member = authService.findOAuthMember(oAuthId, oAuthDomain);
        if(member == null){
            member = Member.createMember(oAuthId, OAuthDomain.KAKAO, provider + oAuthId);
            authService.addMember(member); // 동시 첫 로그인 확률 매우 낮음 + unique constraint(oauth_id, oauth_domain) 를 안걸어둬서 문제 없음.
        }

        // 4. JWT 생성 (글로런 서비스 이용을 위한 인증 토큰)
        String userAccessToken = jwtUtils.createAccessToken(member.getId());
        String userRefreshToken = authService.addAuthorization(member.getId());

        // 5. 쿠키 발급
        jwtUtils.issueAccessTokenAndRefreshToken(response, userAccessToken, userRefreshToken);
        return "redirect:" + redirectPath;
    }

}
