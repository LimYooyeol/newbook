package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.domain.OAuthDomain;
import com.glolearn.newbook.domain.RefreshToken;
import com.glolearn.newbook.repository.RefreshTokenRepository;
import com.glolearn.newbook.repository.MemberRepository;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    /*
        #shorts :
            회원가입 처리
     */
    @Transactional
    public void addMember(Member member){
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member findOAuthMember(String oAuthId, OAuthDomain oAuthDomain){
        return memberRepository.findByOAuthIdAndOAuthDomain(oAuthId, oAuthDomain);
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
        RefreshToken authInfo = RefreshToken.createRefreshToken(refreshTokenId, member, expires.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        refreshTokenRepository.save(authInfo);

        return refreshToken;
    }

    @Transactional
    public void removeAuthorization(String refreshToken){
        if(refreshToken == null){
            return;
        }

        String refreshTokenId = jwtUtils.getRefreshTokenId(refreshToken);
        refreshTokenRepository.deleteById(refreshTokenId);
    }
}