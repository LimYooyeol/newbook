package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Authorization;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.domain.OAuthDomain;
import com.glolearn.newbook.repository.AuthorizationRepository;
import com.glolearn.newbook.repository.MemberRepository;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final AuthorizationRepository authorizationRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public void addMember(Member member){
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member findOAuthMember(String oAuthId, OAuthDomain oAuthDomain){
        return memberRepository.findByOAuthIdAndOAuthDomain(oAuthId, oAuthDomain);
    }

    @Transactional
    public String addAuthorization(Long memberId){
        String refreshTokenId = UUID.randomUUID().toString();

        String refreshToken = jwtUtils.createRefreshToken(refreshTokenId);
        Authorization authorization = new Authorization(refreshTokenId, memberId);
        authorizationRepository.save(authorization);


        return refreshToken;
    }

    @Transactional
    public void removeAuthorization(String refreshToken){
        if(refreshToken == null){
            return;
        }

        String refreshTokenId = jwtUtils.getRefreshTokenId(refreshToken);
        authorizationRepository.deleteById(refreshTokenId);
    }
}
