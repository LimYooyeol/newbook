package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.RollbackException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;

    @Test
    public void 회원추가_및_조회_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.KAKAO, "test");

        //when
        memberService.addMember(member);
        Member findMember = memberService.findMember(member.getId());

        //then
        assertNotNull(findMember);
        assertTrue(member == findMember);
    }

    @Test
    public void 회원삽입_OAuth_중복_테스트(){
        //given
        Member member1 = Member.createMember("test1", OauthDomain.KAKAO, "홍길동");
        Member member2 = Member.createMember("test1", OauthDomain.KAKAO, "김갑동");

        memberService.addMember(member1);

        //when, then
        assertThrows(DataIntegrityViolationException.class, () -> memberService.addMember(member2));
    }

    @Test
    public void 회원삽입_닉네임_중복_테스트(){
        //given
        Member member1 = Member.createMember("test1", OauthDomain.KAKAO, "홍길동");
        Member member2 = Member.createMember("test1", OauthDomain.NAVER, "홍길동");

        memberService.addMember(member1);

        //when, then
        assertThrows(DataIntegrityViolationException.class, () -> memberService.addMember(member2));
    }

    @Test
    public void 회원조회_NULL_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.KAKAO, "홍길동");

        //when
        Member findMember = memberService.findMember(1L);

        //then
        assertNull(findMember);
    }

    @Test
    public void 회원조회_OAuth_정보(){
        //given
        Member member1 = Member.createMember("test1", OauthDomain.KAKAO, "홍길동");
        Member member2 = Member.createMember("test2", OauthDomain.KAKAO, "김갑동");

        //when
        memberService.addMember(member1);
        memberService.addMember(member2);

        Member findMember = memberService.findByOauthIdAndOauthDomain("test1", OauthDomain.KAKAO);

        //then
        assertNotNull(findMember);
        assertEquals("홍길동", findMember.getNickname());
        assertTrue(member1 == findMember);
    }

    @Test
    public void 회원조회_OAuth정보_NULL_테스트(){
        //given
        Member member = Member.createMember("test1", OauthDomain.KAKAO, "홍길동");

        //when
        Member findMember1 = memberService.findByOauthIdAndOauthDomain("test", OauthDomain.KAKAO);
        Member findMember2 = memberService.findByOauthIdAndOauthDomain("test1", OauthDomain.NAVER);

        //then
        assertNull(findMember1);
        assertNull(findMember2);
    }
}