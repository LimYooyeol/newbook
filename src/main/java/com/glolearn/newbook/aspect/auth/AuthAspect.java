package com.glolearn.newbook.aspect.auth;

import com.glolearn.newbook.domain.Auth.AuthInfo;
import com.glolearn.newbook.repository.AuthInfoRepository;
import com.glolearn.newbook.service.AuthInfoService;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {
    private final JwtUtils jwtUtils;
    private final AuthInfoRepository authInfoRepository;
    private final AuthInfoService authService;


    /*
        #shorts :
            인증이 필요한 요청 앞에서 동작
            token 값을 보고 UserContext 에 회원번호 기록
     */
    @Before("@annotation(com.glolearn.newbook.annotation.Auth)")
    public void authorization(JoinPoint joinPoint) {
        UserContext.clear();    // 쓰레드 풀 사용으로 인한 오류 방지

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // access token 확인
        Cookie accessTokenCookie = WebUtils.getCookie(request, "access_token");
        String accessToken = (accessTokenCookie == null)? null : accessTokenCookie.getValue();

        // refresh token 확인
        Cookie refreshTokenCookie = WebUtils.getCookie(request, "refresh_token");
        String refreshToken = (refreshTokenCookie == null)? null : refreshTokenCookie.getValue();



        if(accessToken != null && jwtUtils.validate(accessToken)){
            // access token 으로 인증

            UserContext.setCurrentMember(jwtUtils.getSub(accessToken));
        }else if(refreshToken != null && jwtUtils.validate(refreshToken)){
            // refresh token 으로 인증

            Optional<AuthInfo> findAuth = authInfoRepository.findById(jwtUtils.getRefreshTokenId(refreshToken));

            if(findAuth.isPresent()){   // refresh token 으로 서버인증까지 성공한 경우
                UserContext.setCurrentMember(findAuth.get().getMember().getId());
                UserContext.setReissueFlag();
            }
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
        if(UserContext.getReissueFlag() == true){

            String userAccessToken = jwtUtils.createAccessToken(memberId);
            String userRefreshToken = jwtUtils.createRefreshToken();
            authService.addAuthInfo(userRefreshToken, memberId);

            jwtUtils.issueAccessTokenAndRefreshToken(response, userAccessToken, userRefreshToken);
        }

        UserContext.clear();
    }


}
