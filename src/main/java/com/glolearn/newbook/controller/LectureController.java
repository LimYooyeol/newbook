package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.lecture.LectureRegisterDto;
import com.glolearn.newbook.dto.lecture.LectureUpdateDto;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.CourseService;
import com.glolearn.newbook.service.LectureService;
import com.glolearn.newbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class LectureController {
    private final MemberService memberService;
    private final CourseService courseService;
    private final LectureService lectureService;


    // 강의 등록
    @PostMapping("/course/{courseId}/lecture")
    @Auth
    public String registerLecture(
            @PathVariable(name = "courseId") Long courseId,
            @Valid LectureRegisterDto lectureRegisterDto,
            BindingResult result,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {throw new InvalidAccessException("로그인 후 등록이 가능합니다.");}

        // 유효성 검사
        if(result.hasErrors()){
            model.addAttribute("courseId", courseId);
            model.addAttribute("nickname", member.getNickname());

            return "/course/lecture/registerForm";
        }

        // 인가
        Course course = courseService.findById(courseId);
        if(course == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
        if(course.getLecturer().getId() != member.getId()) {throw new InvalidAccessException("작성 권한이 없습니다.");}

        // 등록
        Long lectureId = lectureService.addLecture(courseId, lectureRegisterDto);

        return "redirect:/lecturer/course/" + courseId + "/" + lectureId;
    }

    @PutMapping("/course/{courseId}/{lectureId}")
    @Auth
    public String modifyLecture(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "lectureId") Long lectureId,
            @Valid LectureUpdateDto lectureUpdateDto,
            BindingResult result,
            Model model
    ){
        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) { return "redirect:/login";}

        //유효성 검사
        if(result.hasErrors()){
            model.addAttribute("nickname", member.getNickname());
            model.addAttribute("courseId", courseId);
            model.addAttribute("lectureId", lectureId);
            return "/course/lecture/modifyForm";
        }

        //강의 조회
        Lecture findLecture = lectureService.findById(lectureId);
        if(findLecture == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //인가
        if(findLecture.getCourse().getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("수정 권한이 없습니다.");
        }

        //수정
        lectureService.modifyLecture(lectureId, lectureUpdateDto);

        return "redirect:/lecturer/course/" + courseId + "/" + lectureId;
    }

    @DeleteMapping("/course/{courseId}/{lectureId}")
    @Auth
    public String deleteLecture(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "lectureId") Long lectureId
    ){
        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) { return "redirect:/login";}

        //강의 조회
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //인가
        if(lecture.getCourse().getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("삭제 권한이 없습니다.");
        }

        //삭제
        lectureService.removeById(lectureId);

        return "redirect:/lecturer/course/"+courseId;
    }


}
