package com.glolearn.newbook.controller;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.oauth.KakaoOauthProvider;
import com.glolearn.newbook.oauth.OauthProvider;
import com.glolearn.newbook.service.AuthInfoService;
import com.glolearn.newbook.service.MemberService;
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
    private final AuthInfoService authInfoService;
    private final MemberService memberService;
    private final KakaoOauthProvider kakaoOauthProvider;
    private final JwtUtils jwtUtils;

    // 로그인 페이지
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    /*
        #shorts:
            로그아웃 (token 만료 + DB refresh token 제거)
     */
    @DeleteMapping("/oauth/token")
    public String logout(HttpServletResponse response,
            @CookieValue(value = "refresh_token", required = false) String originalRefreshToken
    ){
        // DB 에서 refresh token 제거
        try{
            authInfoService.removeAuthInfo(originalRefreshToken);
        }catch (Exception e){

        }finally {
            // access token 과 refresh token 을 모두 만료 처리
            ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                    .path("/").httpOnly(true).maxAge(0).sameSite("strict").build();
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                    .path("/").httpOnly(true).maxAge(0).sameSite("strict").build();
            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return "redirect:/";
        }
    }


    /*
        #shorts :
            OAuth 인증 시 access code 를 들고 redirect 되는 포인트.
            access token 및 refresh token 발급 (user -> ours)
     */
    @GetMapping("/oauth/{provider}/token")
    public String oauth(
            @PathVariable("provider") String provider,
            @RequestParam(name = "code") String accessCode,
            @RequestParam(name = "state", defaultValue = "/") String redirectPath,
            HttpServletResponse response
    ){
        OauthProvider oauthProvider;
        OauthDomain oauthDomain;

        switch (provider){
            case "kakao" :
                oauthProvider = kakaoOauthProvider;
                oauthDomain = OauthDomain.KAKAO;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        String oAuthAccessToken;
        String oAuthId;

        // 1. OAuthId 얻기
        JSONObject httpBody = new JSONObject();
        oAuthAccessToken = oauthProvider.getAccessToken(accessCode);
        oAuthId = oauthProvider.getOAuthId(oAuthAccessToken);

        // 2. oAuthId 얻은 후 바로 (oauth domain 접근 위한) access token 만료
        oauthProvider.expireAccess(oAuthAccessToken);

        // 3. 회원조회 (새로운 회원이라면 추가)
        Member member = memberService.findByOauthIdAndOauthDomain(oAuthId, oauthDomain);
        if(member == null){
            member = Member.createMember(oAuthId, oauthDomain, provider + oAuthId);
            memberService.addMember(member);
        }

        // 4. JWT 생성 (글로런 서비스 이용을 위한 인증 토큰)
        String userAccessToken = jwtUtils.createAccessToken(member.getId());
        String userRefreshToken = jwtUtils.createRefreshToken();
        authInfoService.addAuthInfo(userRefreshToken, member.getId());

        // 5. 쿠키 발급
        jwtUtils.issueAccessTokenAndRefreshToken(response, userAccessToken, userRefreshToken);
        return "redirect:" + redirectPath;
    }

}
