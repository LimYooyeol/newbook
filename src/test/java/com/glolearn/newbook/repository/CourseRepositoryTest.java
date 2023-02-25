package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.*;
import com.glolearn.newbook.dto.course.CourseBriefDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.awt.print.Pageable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseRepositoryTest {

    @Autowired private CourseRepository courseRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    @Transactional
    public void 코스_추가_테스트(){
        // given
        Member member = memberRepository.findById(1L);
        Course course = Course.createCourse(member, "강의1", "소개1",
                false, Category.Development, "/image1");

        // when
        courseRepository.save(course);

        // then
        assertEquals(course, courseRepository.findById(course.getId()).get());
    }

    @Test
    @Transactional
    public void 인기코스_조회_테스트(){
        // given
        PageRequest request = PageRequest.of(0, 5);

        // when
        List<CourseBriefDto> popularCourseList = courseRepository.findCourseBriefByNumStudentDesc(request);

        // then
    }

}