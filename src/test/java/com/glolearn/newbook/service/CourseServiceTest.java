package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.CourseRegisterDto;
import com.glolearn.newbook.dto.course.CourseUpdateDto;
import com.glolearn.newbook.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CourseServiceTest {
    @Autowired MemberService memberService;
    @Autowired CourseService courseService;
    @Autowired
    EntityManager em;

    @Test
    public void 코스_추가_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.NAVER, "홍길동");
        CourseRegisterDto courseRegisterDto = new CourseRegisterDto();
        courseRegisterDto.setTitle("코스1");
        courseRegisterDto.setIntroduction("코스1에 대한 설명");
        courseRegisterDto.setIsPublished(false);
        courseRegisterDto.setCategory(Category.Development);
        courseRegisterDto.setCover("/temp/temp");
        memberService.addMember(member);

        //when
        Long courseId = courseService.addCourse(member.getId(), courseRegisterDto);
        Course findCourse = courseService.findById(courseId);

        //then
        assertNotNull(findCourse);
        assertEquals(findCourse.getIntroduction().getIntroduction(), "코스1에 대한 설명");
    }

    @Test
    public void 없는코스_조회_테스트(){
        //given

        //when
        Course course = courseService.findById(-1L);

        //then
        assertNull(course);
    }

    @Test
    public void 코스_삭제_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.NAVER, "홍길동");
        CourseRegisterDto courseRegisterDto = new CourseRegisterDto();
        courseRegisterDto.setTitle("코스1");
        courseRegisterDto.setIntroduction("코스1에 대한 설명");
        courseRegisterDto.setIsPublished(false);
        courseRegisterDto.setCategory(Category.Development);
        courseRegisterDto.setCover("/temp/temp");
        memberService.addMember(member);
        Long courseId = courseService.addCourse(member.getId(), courseRegisterDto);

        //when
        courseService.removeById(courseId);

        //then
        assertNull(courseService.findById(courseId));
    }

    @Test
    public void 없는코스_삭제_테스트(){
        //given

        //when
        assertThrows(EmptyResultDataAccessException.class,
                () -> courseService.removeById(-1L));
    }

    @Test
    public void 코스_수정_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.NAVER, "홍길동");
        CourseRegisterDto courseRegisterDto = new CourseRegisterDto();
        courseRegisterDto.setTitle("코스1");
        courseRegisterDto.setIntroduction("코스1에 대한 설명");
        courseRegisterDto.setIsPublished(false);
        courseRegisterDto.setCategory(Category.Development);
        courseRegisterDto.setCover("/temp/temp");
        memberService.addMember(member);
        Long courseId = courseService.addCourse(member.getId(), courseRegisterDto);

        CourseUpdateDto courseUpdateDto = new CourseUpdateDto();
        courseUpdateDto.setTitle("코스2");
        courseUpdateDto.setIntroduction("코스2에 대한 설명");
        courseUpdateDto.setCategory(courseRegisterDto.getCategory());
        courseUpdateDto.setIsPublished(courseRegisterDto.getIsPublished());
        courseUpdateDto.setCover(courseUpdateDto.getCover());

        //when
        courseService.modifyCourse(courseId, courseUpdateDto);
        Course course = courseService.findById(courseId);

        //then
        assertNotNull(course);
        assertEquals(course.getTitle(), "코스2");
        assertEquals(course.getIntroduction().getIntroduction(), "코스2에 대한 설명");
    }

    @Test
    public void 인기코스_조회_테스트(){
        //given
        int size = 5;
        for(int i = 0; i < size; i++){
            Member member = Member.createMember("test" + i, OauthDomain.NAVER, "홍길동" + i);
            memberService.addMember(member);
            CourseRegisterDto courseRegisterDto = new CourseRegisterDto();
            courseRegisterDto.setTitle("코스" + i);
            courseRegisterDto.setIntroduction("코스에 대한 설명" + i);
            courseRegisterDto.setIsPublished(false);
            courseRegisterDto.setCategory(Category.Development);
            courseRegisterDto.setCover("/temp/temp");
            courseService.addCourse(member.getId(), courseRegisterDto);
        }

        em.clear();

        //when
        List<Course> popularCourses = courseService.findPopularCourseList();
        for(Course c : popularCourses){
            System.out.println(c.getLecturer().getNickname());
            System.out.println(c.getIntroduction().getIntroduction());
        }

        //then
        assertNotNull(popularCourses);
        assertEquals((size <5 ? size : 5), popularCourses.size());
    }
}