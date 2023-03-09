package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.*;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.CourseService;
import com.glolearn.newbook.service.MemberService;
import javassist.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final MemberService memberService;

    @GetMapping("/course/register")
    @Auth
    public String register(Model model){
        // 0. 비로그인 처리
        if(UserContext.getCurrentMember() == null){
            return "redirect:/login";
        }

        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {throw new InvalidAccessException("Token with non-existing member.");}

        // 1. model 에 데이터 전달
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseRegisterDto", new CourseRegisterDto());

        return "/course/registerForm";
    }

    @PostMapping("/course/register")
    @Auth
    public String register(@Valid CourseRegisterDto courseRegisterDto, BindingResult result, Model model){
        if(result.hasErrors()){
            Member member = memberService.findMember(UserContext.getCurrentMember());
            model.addAttribute("nickname", member.getNickname());

            return "/course/registerForm";
        }

        // 0. 비로그인 처리
        if(UserContext.getCurrentMember() == null) {return "redirect:/login";}
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){throw new InvalidAccessException("Token with non-existing member.");}

        // 2. 등록
        courseService.addCourse(member.getId(), courseRegisterDto);
        return "redirect:/lecturer/courses/all";
    }

    @GetMapping("/course/{id}")
    @Auth
    public String detail(@PathVariable(name = "id") Long courseId,
                         Model model){
        Member member;

        // 로그인 정보 처리
        if(UserContext.getCurrentMember() != null) {
            member = memberService.findMember(UserContext.getCurrentMember());
            if (member != null) {
                model.addAttribute("nickname", member.getNickname());
            }
        }

        Course course = courseService.findById(courseId);
        if(course == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        model.addAttribute("courseDetailsDto", new CourseDetailsDto(course));
        return "/course/details";
    }

    @GetMapping("/courses")
    public String initialCourseList(){
        return "redirect:/courses/all";
    }

    @GetMapping("/courses/{category}")
    @Auth
    public String courseList(
            @PathVariable(name = "category", required = false) String category,
            @RequestParam(required = false, defaultValue = "recent") String sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "search", required = false) String search,
            Model model
    ){
        Member member;

        // 로그인 정보 처리
        if(UserContext.getCurrentMember() != null) {
            member = memberService.findMember(UserContext.getCurrentMember());
            if (member != null) {
                model.addAttribute("nickname", member.getNickname());
            }
        }

        // 검색 조건 DTO
        CourseSearchDto courseSearchDto = new CourseSearchDto();
        courseSearchDto.setCategory(Category.of(category));
        courseSearchDto.setSort(Sort.of(sort));
        courseSearchDto.setPageNum(page);
        courseSearchDto.setSearch(search);

        courseSearchDto.setPageSize(6);

        // 검색
        int maxPage = courseService.findMaxPage(courseSearchDto);
        if(page < 1 || page > maxPage){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }


        List<CoursePreviewDto> courses = courseService.findCourses(courseSearchDto);

        model.addAttribute("maxPage", maxPage);
        model.addAttribute("courseSearchDto", courseSearchDto);
        model.addAttribute("courses", courses);
        return "/course/list";
    }

    @PutMapping("/course/{id}")
    @Auth
    public String updateCourse(
            @PathVariable(name = "id") Long id,
            @Valid CourseUpdateDto courseUpdateDto,
            BindingResult result,
            Model model){
        // 인증 처리
        if(UserContext.getCurrentMember() == null){ return "redirect:/login";}
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){ throw new InvalidAccessException("존재하지 않는 유저에 대한 토큰입니다.");}

        // 코스 조회
        Course course = courseService.findById(id);
        if(course == null) { throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        // 권한 조회
        if(course.getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("수정 권한이 없습니다.");
        }

        courseService.modifyCourse(id, courseUpdateDto);

        return "redirect:/lecturer/course/" + id;
    }

    @DeleteMapping("/course/{id}")
    @Auth
    public String deleteCourse(
            @PathVariable(name = "id")Long id
    ){
        // 인증 처리
        if(UserContext.getCurrentMember() == null){ return "redirect:/login";}
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){ throw new InvalidAccessException("존재하지 않는 유저에 대한 토큰입니다.");}

        // 코스 조회
        Course course = courseService.findById(id);
        if(course == null) { throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        // 권한 조회
        if(course.getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("삭제 권한이 없습니다.");
        }

        courseService.removeById(id);

        return "redirect:/lecturer/courses/all";
    }

//
//    @GetMapping("/course/{courseId}/{lectureId}")
//    @Auth
//    public String lectureDetails(
//            @PathVariable(name = "courseId") String courseId,
//            @PathVariable(name = "lectureId") String lectureId,
//            Model model
//    ){
//        // 0. 비로그인 처리
//        Member member = memberService.findMember(UserContext.getCurrentMember());
//        if(member != null){model.addAttribute("nickname", member.getNickname());}
//
//        return "/course/lecture/details";
//    }
//

}
