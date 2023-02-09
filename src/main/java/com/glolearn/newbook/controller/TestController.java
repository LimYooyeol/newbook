package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.context.UserContext;
import com.glolearn.newbook.domain.Authorization;
import com.glolearn.newbook.repository.AuthorizationRepository;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final AuthorizationRepository authorizationRepository;
    private final JwtUtils jwtUtils;



    @GetMapping("/api/test")
    @Auth
    public String test(){
        if(UserContext.unAuthorized()){
            return "unAuthorized";
        }

        return UserContext.getCurrentMember().toString() + "로 로그인";
    }

    @GetMapping("/api/test/user/me")
    public String getMe(@CookieValue(value = "access_token", required = false) String accessToken,
                        @CookieValue(value = "refresh_token", required = false) String refreshToken,
                        HttpServletRequest httpRequest,
                        HttpServletResponse httpResponse) throws IOException {
        JSONObject response = new JSONObject();

        Long memberId;
        if(accessToken != null && (memberId = jwtUtils.getSub(accessToken))  != null){
            response.put("member_id", memberId);
        }else if(refreshToken != null){
            Authorization findAuth = authorizationRepository.findByTokenId(jwtUtils.getRefreshTokenId(refreshToken));

            if(findAuth != null){
                String userAccessToken = jwtUtils.createAccessToken(findAuth.getMemberId());
                ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", userAccessToken)
                        .path("/").httpOnly(true).maxAge(60*60).sameSite("strict").build();


                httpResponse.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
                response.put("redirect_uri", "/");
            }
        }

        return response.toString();
    }
}
