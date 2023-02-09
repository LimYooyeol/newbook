package com.glolearn.newbook.aspect;

import com.glolearn.newbook.context.UserContext;
import com.glolearn.newbook.domain.Authorization;
import com.glolearn.newbook.exception.InvalidAccessTokenException;
import com.glolearn.newbook.repository.AuthorizationRepository;
import com.glolearn.newbook.service.LoginService;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {
    private final JwtUtils jwtUtils;

    private final AuthorizationRepository authorizationRepository;
    private final LoginService loginService;

    @Before("@annotation(com.glolearn.newbook.annotation.Auth)")
    public void authorization() throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Cookie accessTokenCookie = WebUtils.getCookie(request, "access_token");
        String accessToken = (accessTokenCookie == null)? null : accessTokenCookie.getValue();

        Cookie refreshTokenCookie = WebUtils.getCookie(request, "refresh_token");
        String refreshToken = (refreshTokenCookie == null)? null : refreshTokenCookie.getValue();


        if(jwtUtils.validate(accessToken)){
            // access
            UserContext.setCurrentMember(jwtUtils.getSub(accessToken));
            UserContext.setReissueFlag(false);
        }else if(jwtUtils.validate(refreshToken)){
            // refresh token 으로 인증
            Authorization findAuth = authorizationRepository.findByTokenId(jwtUtils.getRefreshTokenId(refreshToken));
            if(findAuth != null){   // refresh token 으로 서버인증까지 성공한 경우
                UserContext.setCurrentMember(findAuth.getMemberId());
                UserContext.setReissueFlag(true);
            }
        }else{
            UserContext.setUnAuthorized();
            UserContext.setReissueFlag(false);
        }
    }

    @After("@annotation(com.glolearn.newbook.annotation.Auth)")
    public void endAuthorization(){
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();


        Long memberId = UserContext.getCurrentMember();

        // token 재발행 -> refresh token 으로 인증한 경우
        if(UserContext.getReissueFlag() != null && UserContext.getReissueFlag() == true){

            String userAccessToken = jwtUtils.createAccessToken(memberId);
            String userRefreshToken = loginService.addAuthorization(memberId);

            HttpHeaders headers = new HttpHeaders();

            ResponseCookie newAccessTokenCookie = ResponseCookie.from("access_token", userAccessToken)
                    .path("/").httpOnly(true).maxAge(jwtUtils.ACCESS_TOKEN_LIFESPAN_SEC)
                    .sameSite("strict").build();
            ResponseCookie newRefreshTokenCookie = ResponseCookie.from("refresh_token", userRefreshToken)
                    .path("/").httpOnly(true).maxAge(jwtUtils.REFRESH_TOKEN_LIFESPAN_SEC)
                    .sameSite("strict").build();

            response.addHeader(HttpHeaders.SET_COOKIE, newAccessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString());
        }

        UserContext.clear();
    }


}
