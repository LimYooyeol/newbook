package com.glolearn.newbook.aspect;

import com.glolearn.newbook.context.UserContext;
import com.glolearn.newbook.domain.AuthInfo;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.repository.AuthInfoRepository;
import com.glolearn.newbook.service.AuthService;
import com.glolearn.newbook.service.MemberService;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {
    private final JwtUtils jwtUtils;

    private final AuthInfoRepository authInfoRepository;
    private final AuthService authService;


    /*
        #shorts :
            인증이 필요한 요청 앞에서 동작
            token 값을 보고 UserContext 에 회원번호 기록
     */
    @Before("@annotation(com.glolearn.newbook.annotation.Auth)")
    public void authorization() throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Cookie accessTokenCookie = WebUtils.getCookie(request, "access_token");

        String accessToken = (accessTokenCookie == null)? null : accessTokenCookie.getValue();

        Cookie refreshTokenCookie = WebUtils.getCookie(request, "refresh_token");
        String refreshToken = (refreshTokenCookie == null)? null : refreshTokenCookie.getValue();


        if(jwtUtils.validate(accessToken)){
            // access token 으로 인증
            UserContext.setCurrentMember(jwtUtils.getSub(accessToken));
            UserContext.setReissueFlag(false);
        }else if(jwtUtils.validate(refreshToken)){
            // refresh token 으로 인증
            AuthInfo findAuth = authInfoRepository.findByTokenId(jwtUtils.getRefreshTokenId(refreshToken));
            if(findAuth != null){   // refresh token 으로 서버인증까지 성공한 경우
                UserContext.setCurrentMember(findAuth.getMember().getId());
                UserContext.setReissueFlag(true);
            }
        }else{ // 인증 실패
            UserContext.setUnAuthorized();
            UserContext.setReissueFlag(false);
        }

    }

    /*
        #shorts :
            인증 후 ThreadLocal 초기화
            Refresh token 으로 인증한 경우 access token 과 refresh token 재발행
     */
    @After("@annotation(com.glolearn.newbook.annotation.Auth)")
    public void endAuthorization(){
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();

        Long memberId = UserContext.getCurrentMember();

        // refresh token 으로 인증한 경우 -> 토큰 재발행
        if(UserContext.getReissueFlag() != null && UserContext.getReissueFlag() == true){

            String userAccessToken = jwtUtils.createAccessToken(memberId);
            String userRefreshToken = authService.addAuthorization(memberId);

            jwtUtils.issueAccessTokenAndRefreshToken(response, userAccessToken, userRefreshToken);
        }

        UserContext.clear();
    }


}
