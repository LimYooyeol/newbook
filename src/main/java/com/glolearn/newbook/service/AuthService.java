package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Auth.AuthInfo;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.repository.AuthInfoRepository;
import com.glolearn.newbook.repository.MemberRepository;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final AuthInfoRepository authInfoRepository;
    private final JwtUtils jwtUtils;

    /*
        #shorts :
            회원가입 처리
     */
    @Transactional
    public void addMember(Member member){
        if(member == null){
            return;
        }
        memberRepository.save(member);
    }

    /*
        #return:
            없으면 -> null
            있으면 -> Member 객체
     */
    @Transactional(readOnly = true)
    public Member findOAuthMember(String oAuthId, OauthDomain oAuthDomain){
        if(oAuthId == null || oAuthDomain == null){
            return null;
        }

        return memberRepository.findByOauthIdAndOauthDomain(oAuthId, oAuthDomain);
    }

    /*
        #shorts :
            refresh token 을 생성하면서 DB 에도 그 ID를 저장
        #to do  :
            코드 정리하기
     */
    @Transactional
    public String addAuthorization(Long memberId){
        String refreshTokenId = UUID.randomUUID().toString();
        Member member = memberRepository.findById(memberId);

        String refreshToken = jwtUtils.createRefreshToken(refreshTokenId);
        Date expires = new Date(new Date().getTime() + jwtUtils.REFRESH_TOKEN_LIFESPAN_MILLI);
        AuthInfo authInfo = AuthInfo.createAuthInfo(refreshTokenId, member, expires.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        authInfoRepository.save(authInfo);

        return refreshToken;
    }

    @Transactional
    public void removeAuthorization(String refreshToken){
        if(refreshToken == null){
            return;
        }

        String refreshTokenId = jwtUtils.getRefreshTokenId(refreshToken);
        if(refreshTokenId == null){ // RefreshToken 이 유효하지 않은 경우
            return;
        }

        try{
            authInfoRepository.deleteById(refreshTokenId);
        }catch(EmptyResultDataAccessException e){
            return;
        }
    }
}
