package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.context.UserContext;
import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.CourseRegisterDto;
import com.glolearn.newbook.service.CourseService;
import com.glolearn.newbook.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final MemberService memberService;

    @GetMapping("/course/register")
    @Auth
    public String register(Model model){
        // 0. 비로그인 처리
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member != null){model.addAttribute("nickname", member.getNickname());}
        else{throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);}

        // 1. model 에 데이터 전달
        model.addAttribute("courseRegisterDto", new CourseRegisterDto());

        return "/course/registerForm";
    }

    /*
        #to do: @Valid 처리
     */
    @PostMapping("/course/register")
    @Auth
    public String register(CourseRegisterDto courseRegisterDto){
        // 0. 비로그인 처리
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);}

        // 1. Course 생성
        Course course = Course.createCourse(
                member, courseRegisterDto.getCourseName(), courseRegisterDto.getIntroduction(),
                courseRegisterDto.getPublished(), courseRegisterDto.getCategory(), courseRegisterDto.getCover()
        );

        // 2. 등록
        courseService.addCourse(course);

        return "redirect:/";
    }

    @GetMapping("/course/list")
    @Auth
    public String list(){
        // 0. 비로그인 처리
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);}

        return "/course/list";
    }

    @GetMapping("/lecturer")
    @Auth
    public String lecturer(Model model){
        // 0. 비로그인 처리
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member != null){model.addAttribute("nickname", member.getNickname());}

        return "/lecturer/course/list";
    }

    @GetMapping("/lecturer/course/{id}")
    @Auth
    public String manageLecture(Model model,
                                @PathVariable(name = "id") Long id){
        // 0. 비로그인 처리
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member != null){model.addAttribute("nickname", member.getNickname());}

        return "/lecturer/course/manage";
    }

}
