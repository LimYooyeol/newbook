package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.domain.OAuthDomain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseRepositoryTest {

    @Autowired private CourseRepository courseRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    @Transactional
    public void 코스추가_테스트(){
        // given
        Member member = memberRepository.findById(1L);
        Course course = Course.createCourse(member, "강의1", "소개1",
                false, Category.Development, "/image1");

        // when
        courseRepository.save(course);

        // then
        assertEquals(course, courseRepository.findById(course.getId()).get());
    }

}