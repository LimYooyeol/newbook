package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.CoursePreviewDto;
import com.glolearn.newbook.dto.course.CourseRegisterDto;
import com.glolearn.newbook.dto.course.CourseSearchDto;
import com.glolearn.newbook.dto.course.CourseUpdateDto;
import com.glolearn.newbook.repository.CourseRepository;
import com.glolearn.newbook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<CoursePreviewDto> findPopularCourseList(){
        Pageable pageable = PageRequest.of(0, 4);
        List<Course> popularCourses = courseRepository.findAllByOrderByNumStudentDesc(pageable);
        List<CoursePreviewDto> result = popularCourses.stream()
                .map(c -> new CoursePreviewDto(c))
                .collect(Collectors.toList());

        return result;
    }

    // 코스 리스트 조회
    public List<CoursePreviewDto> findCourses(CourseSearchDto courseSearchDto){
        List<Course> findCourses = courseRepository.findCourses(courseSearchDto);
        List<CoursePreviewDto> result = findCourses.stream()
                    .map(c -> new CoursePreviewDto(c))
                    .collect(Collectors.toList());
        return result;
    }

    public int findMaxPageByLecturer(Long lecturerId, CourseSearchDto courseSearchDto){
        int numCourses = Math.toIntExact(courseRepository.countCoursesByLecturer(lecturerId, courseSearchDto));

        return (numCourses-1)/courseSearchDto.getPageSize() + 1;
    }

    // 최대 페이지 수 조회
    public int findMaxPage(CourseSearchDto courseSearchDto){
        int numCourses = Math.toIntExact(courseRepository.countCourses(courseSearchDto));

        return (numCourses - 1)/courseSearchDto.getPageSize() + 1;
    }

    public List<CoursePreviewDto> findCoursesByLecturer(Long lecturerId, CourseSearchDto courseSearchDto){
        List<Course> lecturerCourses = courseRepository.findCoursesByLecturer(lecturerId, courseSearchDto);
        List<CoursePreviewDto> result = lecturerCourses.stream()
                .map(c -> new CoursePreviewDto(c))
                .collect(Collectors.toList());

        return result;
    }

}
