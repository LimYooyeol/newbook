package com.glolearn.newbook.service;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.CourseRegisterDto;
import com.glolearn.newbook.dto.lecture.LecturePreviewDto;
import com.glolearn.newbook.dto.lecture.LectureRegisterDto;
import com.glolearn.newbook.dto.lecture.LectureUpdateDto;
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
class LectureServiceTest {
    @Autowired MemberService memberService;
    @Autowired CourseService courseService;
    @Autowired LectureService lectureService;

    @Autowired
    EntityManager em;


    @Test
    public void 강의_추가_조회_테스트(){
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

        em.flush();
        em.clear();
        System.out.println("============지연로딩 테스트용-1 ===========");
        Lecture lecture = lectureService.findById(lectureId);

        //then
        assertNotNull(lecture);
        System.out.println("============지연로딩 테스트용-2 ===========");
        assertEquals(lecture.getContents().getContents(), "강의 내용");
        System.out.println("============지연로딩 테스트용-3 ===========");
        assertEquals(lecture.getCourse().getLecturer().getNickname(), "홍길동");
    }

    @Test
    public void 강의_조회_예외_테스트(){
        //given

        //when, then
        assertNull(lectureService.findById(-1L));
    }


    @Test
    public void 강의목록_조회_테스트(){
        //given

        //회원 추가
        Member member = Member.createMember("test", OauthDomain.KAKAO, "홍길동");
        memberService.addMember(member);

        //코스 추가
        CourseRegisterDto courseRegisterDto = new CourseRegisterDto();
        courseRegisterDto.setTitle("코스");
        courseRegisterDto.setIntroduction("코스에 대한 설명");
        courseRegisterDto.setIsPublished(false);
        courseRegisterDto.setCategory(Category.Development);
        courseRegisterDto.setCover("/temp/temp");
        Long courseId = courseService.addCourse(member.getId(), courseRegisterDto);

        for(int i = 0; i< 5; i ++){
            LectureRegisterDto lectureRegisterDto = new LectureRegisterDto();
            lectureRegisterDto.setTitle("강의" + i);
            lectureRegisterDto.setContents("강의 내용" + i);
            lectureService.addLecture(courseId, lectureRegisterDto);
        }

        em.flush();
        em.clear();


        //when
        System.out.println("============ 지연 로딩 테스트1 ===========");
        List<LecturePreviewDto> lectures = lectureService.findAllByCourseId(courseId);

        //then
        assertEquals(5, lectures.size());
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
        assertThrows(IllegalArgumentException.class,
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