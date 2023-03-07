package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.CourseRegisterDto;
import com.glolearn.newbook.dto.lecture.LectureRegisterDto;
import com.glolearn.newbook.dto.lecture.LectureUpdateDto;
import com.glolearn.newbook.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LectureServiceTest {
    @Autowired MemberService memberService;
    @Autowired CourseService courseService;
    @Autowired LectureService lectureService;


    @Test
    public void 강의추가_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.KAKAO, "홍길동");

        CourseRegisterDto courseRegisterDto = new CourseRegisterDto();
        courseRegisterDto.setTitle("코스");
        courseRegisterDto.setIntroduction("코스에 대한 설명");
        courseRegisterDto.setIsPublished(false);
        courseRegisterDto.setCategory(Category.Development);
        courseRegisterDto.setCover("/temp/temp");

        memberService.addMember(member);
        Long courseId = courseService.addCourse(member.getId(), courseRegisterDto);


        LectureRegisterDto lectureRegisterDto = new LectureRegisterDto();
        lectureRegisterDto.setTitle("강의");
        lectureRegisterDto.setContents("강의 내용");

        //when
        Long lectureId = lectureService.addLecture(courseId, lectureRegisterDto);
        Lecture lecture = lectureService.findById(lectureId);

        //then
        assertNotNull(lecture);
        assertEquals(lecture.getContents().getContents(), "강의 내용");
        assertEquals(lecture.getCourse().getLecturer().getNickname(), "홍길동");
    }

    @Test
    public void 강의_조회_예외_테스트(){
        //given

        //when, then
        assertNull(lectureService.findById(-1L));
    }

    @Test
    public void 강의_삭제_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.KAKAO, "홍길동");

        CourseRegisterDto courseRegisterDto = new CourseRegisterDto();
        courseRegisterDto.setTitle("코스");
        courseRegisterDto.setIntroduction("코스에 대한 설명");
        courseRegisterDto.setIsPublished(false);
        courseRegisterDto.setCategory(Category.Development);
        courseRegisterDto.setCover("/temp/temp");

        memberService.addMember(member);
        Long courseId = courseService.addCourse(member.getId(), courseRegisterDto);


        LectureRegisterDto lectureRegisterDto = new LectureRegisterDto();
        lectureRegisterDto.setTitle("강의");
        lectureRegisterDto.setContents("강의 내용");

        Long lectureId = lectureService.addLecture(courseId, lectureRegisterDto);
        Lecture lecture = lectureService.findById(lectureId);

        //when
        lectureService.removeById(lectureId);

        //then
        assertNull(lectureService.findById(lectureId));
    }

    @Test
    public void 없는강의_삭제_테스트(){
        //given

        //when
        assertThrows(EmptyResultDataAccessException.class,
                () -> lectureService.removeById(-1L));
    }

    @Test
    public void 강의_수정_테스트(){
        //given
        Member member = Member.createMember("test", OauthDomain.KAKAO, "홍길동");

        CourseRegisterDto courseRegisterDto = new CourseRegisterDto();
        courseRegisterDto.setTitle("코스");
        courseRegisterDto.setIntroduction("코스에 대한 설명");
        courseRegisterDto.setIsPublished(false);
        courseRegisterDto.setCategory(Category.Development);
        courseRegisterDto.setCover("/temp/temp");

        memberService.addMember(member);
        Long courseId = courseService.addCourse(member.getId(), courseRegisterDto);


        LectureRegisterDto lectureRegisterDto = new LectureRegisterDto();
        lectureRegisterDto.setTitle("강의");
        lectureRegisterDto.setContents("강의 내용");

        Long lectureId = lectureService.addLecture(courseId, lectureRegisterDto);
        Lecture lecture = lectureService.findById(lectureId);

        LectureUpdateDto lectureUpdateDto = new LectureUpdateDto();
        lectureUpdateDto.setTitle("강의-v2");
        lectureUpdateDto.setContents("강의 내용-v2");

        //when
        lectureService.modifyLecture(lectureId, lectureUpdateDto);

        //then
        Lecture updatedLecture = lectureService.findById(lectureId);
        assertEquals(updatedLecture.getContents().getContents(), "강의 내용-v2");
        assertEquals(updatedLecture.getTitle(), "강의-v2");

    }

}