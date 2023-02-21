package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member findMember(Long id) {
        if(id == null){
            return null;
        }
        return memberRepository.findById(id);
    }
}
