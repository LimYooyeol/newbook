package com.glolearn.newbook.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.JwtSignatureValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    @Value("${secret-key}")
    private String secretKey;   // 외부에 의존적인 값이 있는 경우엔 Bean 으로 동록( vs static)

    public final long ACCESS_TOKEN_LIFESPAN_MILLI = 1000*60*60;
    public final long REFRESH_TOKEN_LIFESPAN_MILLI = 1000*60*60*24*14;

    public final long ACCESS_TOKEN_LIFESPAN_SEC = 60*60;
    public final long REFRESH_TOKEN_LIFESPAN_SEC = 60*60*24*14;

    public String createAccessToken(Long memberId){
        Claims claims = Jwts.claims().setSubject(memberId.toString());
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_LIFESPAN_MILLI))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String refreshTokenId){
        Claims claims = Jwts.claims().setSubject(refreshTokenId);
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_LIFESPAN_MILLI))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /*
        #shorts :
            Token 에서 사용자 ID 추출
        #warning:
            유효성 검사 후 호출하는 것이 안전
     */
    public Long getSub(String accessToken){
        try{
            return Long.parseLong(Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(accessToken)
                .getBody().getSubject());
        }catch (Exception e){
            return null;
        }
    }

    /*
        #shorts :
            Token 에서 토큰 ID 추출
        #warning:
            유효성 검사 후 호출하는 것이 안전
     */
    public String getRefreshTokenId(String refreshToken){
        try{
            return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(refreshToken)
                .getBody().getSubject();
        }catch (Exception e){
            return null;
        }
    }

    // token 자체의 유효성만 검사
    public boolean validate(String token) {
        if(token == null){
            return false;
        }
        try{
            Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
            if(expiration.before(new Date())){
                return false;
            }
        }catch (SignatureException e){
            return false;
        }

        return true;
    }

    /*
        #shorts :
            HttpServletResponse 에 access token 과 refresh token 을 HttpOnlyCookie 로 담아줌
     */
    public void issueAccessTokenAndRefreshToken(HttpServletResponse response,
                                                String userAccessToken,
                                                String userRefreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", userAccessToken)
                .path("/").httpOnly(true).maxAge(ACCESS_TOKEN_LIFESPAN_SEC)
                .sameSite("lax").build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", userRefreshToken)
                .path("/").httpOnly(true).maxAge(REFRESH_TOKEN_LIFESPAN_SEC)
                .sameSite("lax").build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
}
