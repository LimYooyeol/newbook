package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.*;
import com.glolearn.newbook.dto.lecture.LectureDetailsDto;
import com.glolearn.newbook.dto.lecture.LecturePreviewDto;
import com.glolearn.newbook.dto.lecture.LectureRegisterDto;
import com.glolearn.newbook.dto.lecture.LectureUpdateDto;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.CourseService;
import com.glolearn.newbook.service.LectureService;
import com.glolearn.newbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LecturerController {

    private final MemberService memberService;

    private final CourseService courseService;
    private final LectureService lectureService;

    @GetMapping("/lecturer/courses/{category}")
    @Auth
    public String lecturer(
            @PathVariable(name = "category", required = false) String category,
            @RequestParam(required = false, defaultValue = "recent") String sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "search", required = false) String search,
            Model model
    ){
        if(UserContext.getCurrentMember() == null){
            return "redirect:/login";
        }
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){throw new InvalidAccessException("???????????? ?????? ????????? ?????? ???????????????.");}
        
        // ?????? ?????? DTO
        CourseSearchDto courseSearchDto = new CourseSearchDto();
        courseSearchDto.setCategory(Category.of(category));
        courseSearchDto.setSort(Sort.of(sort));
        courseSearchDto.setPageNum(page);
        courseSearchDto.setSearch(search);

        courseSearchDto.setPageSize(6);
        
        // ??????
        int maxPage = courseService.findMaxPageByLecturer(member.getId(), courseSearchDto);
        if(page < 1 || page > maxPage){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<CoursePreviewDto> courses = courseService.findCoursesByLecturer(member.getId(), courseSearchDto);


        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courses", courses);
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("courseSearchDto", courseSearchDto);

        return "/lecturer/course/list";
    }


    @GetMapping("/lecturer/course/{id}")
    @Auth
    public String manageLecture(@PathVariable(name = "id") Long id,
                                Model model){
        // ?????? ??????
        if(UserContext.getCurrentMember() == null){ return "redirect:/login";}
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){ throw new InvalidAccessException("???????????? ?????? ????????? ?????? ???????????????.");}

        // ?????? ??????
        Course course = courseService.findById(id);
        if(course == null) { throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        // ?????? ??????
        if(course.getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("?????? ????????? ????????????.");
        }

        List<LecturePreviewDto> lectures = lectureService.findAllByCourseId(course.getId());



        CourseDetailsDto courseDetailsDto = new CourseDetailsDto(course);

        // ?????? ??????
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("course", courseDetailsDto);
        model.addAttribute("lectures", lectures);

        return "/lecturer/course/manage";
    }

    @GetMapping("/lecturer/course/{id}/modify")
    @Auth
    public String updateCoursePage(
            @PathVariable("id") Long id,
            Model model
    ){
        // ?????? ??????
        if(UserContext.getCurrentMember() == null){ return "redirect:/login";}
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){ throw new InvalidAccessException("???????????? ?????? ????????? ?????? ???????????????.");}

        // ?????? ??????
        Course course = courseService.findById(id);
        if(course == null) { throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        // ?????? ??????
        if(course.getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("?????? ????????? ????????????.");
        }

        CourseUpdateDto courseUpdateDto = new CourseUpdateDto(course);

        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseUpdateDto", courseUpdateDto);

        return "/course/modifyForm";
    }

    // ?????? ?????? ??????
    @GetMapping("/lecturer/course/{courseId}/lecture/register")
    @Auth
    public String lectureRegisterForm(
            @PathVariable(name = "courseId") Long courseId,
            Model model
    ){
        // ??????
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){ return "redirect:/login";}

        // ?????? ??????
        Course course = courseService.findById(courseId);
        if(course == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //??????
        if(member.getId() != course.getLecturer().getId()){throw new InvalidAccessException("?????? ????????? ????????????.");}

        // ????????? ??????
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseId", courseId);
        model.addAttribute("lectureRegisterDto", new LectureRegisterDto());

        return "/course/lecture/registerForm";
    }

    // ?????? ?????? ????????? ??????
    @GetMapping("/lecturer/course/{courseId}/{lectureId}")
    @Auth
    public String lectureDetailPage(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "lectureId") Long lectureId,
            Model model
    ){
        //??????
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        //?????? ??????
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //??????
        if(lecture.getCourse().getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("?????? ????????? ????????????.");
        }

        // ????????? ??????
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("lecture", new LectureDetailsDto(lecture));

        return "/course/lecture/details";
    }


    //?????? ?????? ??????
    @GetMapping("/lecturer/course/{courseId}/{lectureId}/modify")
    @Auth
    public String lectureModifyPage(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "lectureId") Long lectureId,
            Model model
    ){

        //??????
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) { return "redirect:/login";}

        //?????? ??????
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //??????
        if(lecture.getCourse().getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("?????? ????????? ????????????.");
        }

        //?????? ??????
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseId", courseId);
        model.addAttribute("lectureId", lectureId);
        model.addAttribute("lectureUpdateDto", new LectureUpdateDto(lecture));

        return "/course/lecture/modifyForm";
    }

}
