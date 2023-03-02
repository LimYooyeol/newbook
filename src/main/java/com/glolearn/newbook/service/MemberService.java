package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원 추가
    @Transactional
    public void addMember(Member member){
        memberRepository.save(member);
    }

    // 회원 조회(ID)
    public Member findMember(Long id) {
        return memberRepository.findById(id);
    }

    // 회원 조회(OAuth Id && OAuth Domain)
    public Member findByOauthIdAndOauthDomain(String oauthId, OauthDomain oauthDomain) {
        return memberRepository.findByOauthIdAndOauthDomain(oauthId, oauthDomain);
    }
}
