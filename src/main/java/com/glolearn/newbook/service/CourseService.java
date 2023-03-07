package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.CourseRegisterDto;
import com.glolearn.newbook.dto.course.CourseUpdateDto;
import com.glolearn.newbook.repository.CourseRepository;
import com.glolearn.newbook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;

    // 코스 추가
    @Transactional
    public Long addCourse(Long memberId, CourseRegisterDto courseRegisterDto){
        Member member = memberRepository.findById(memberId);
        Course course = Course.createCourse(member, courseRegisterDto);

        courseRepository.save(course);

        return course.getId();
    }

    // 코스 조회
    public Course findById(Long courseId){
        return courseRepository.findById(courseId).orElse(null);
    }


    // 코스 삭제
    @Transactional
    public void removeById(Long courseId){
        courseRepository.deleteById(courseId);
    }

    // 코스 업데이트
    @Transactional
    public void modifyCourse(Long courseId, CourseUpdateDto courseUpdateDto){
        Course course = courseRepository.findById(courseId).orElse(null);
        course.update(courseUpdateDto);
    }

    // 인기 코스 조회
    public List<Course> findPopularCourseList(){
        Pageable pageable = PageRequest.of(0, 5);
        List<Course> popularCourses = courseRepository.findAllByOrderByNumStudentDesc(pageable);

        return popularCourses;
    }

}
