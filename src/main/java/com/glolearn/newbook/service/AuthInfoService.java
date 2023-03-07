package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Auth.AuthInfo;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.repository.AuthInfoRepository;
import com.glolearn.newbook.repository.MemberRepository;
import com.glolearn.newbook.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthInfoService {
    private final MemberRepository memberRepository;
    private final AuthInfoRepository authInfoRepository;
    private final JwtUtils jwtUtils;

    // Refresh token 인증 정보 저장
    public void addAuthInfo(String refreshToken, Long memberId){
        String tokenId = jwtUtils.getRefreshTokenId(refreshToken);
        Date expiration = jwtUtils.getExpiration(refreshToken);

        Member member = memberRepository.findById(memberId);
        AuthInfo authInfo = AuthInfo.createAuthInfo(tokenId, member, expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        authInfoRepository.save(authInfo);
    }

    // refresh token 인증 정보 조회
    @Transactional(readOnly = true)
    public AuthInfo findAuthInfo(String refreshToken){
        String tokenId = jwtUtils.getRefreshTokenId(refreshToken);

        Optional<AuthInfo> authInfo  = authInfoRepository.findById(tokenId);
        if(authInfo.isPresent()){
            return authInfo.get();
        }

        return null;
    }

    // token Id로 Auth info 삭제
    public void removeAuthInfo(String refreshToken){
        String refreshTokenId = jwtUtils.getRefreshTokenId(refreshToken);
        authInfoRepository.deleteById(refreshTokenId);
    }
}
