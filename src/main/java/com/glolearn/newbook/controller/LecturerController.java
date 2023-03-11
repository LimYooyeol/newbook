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
        if(member == null){throw new InvalidAccessException("존재하지 않는 멤버에 대한 토큰입니다.");}
        
        // 검색 조건 DTO
        CourseSearchDto courseSearchDto = new CourseSearchDto();
        courseSearchDto.setCategory(Category.of(category));
        courseSearchDto.setSort(Sort.of(sort));
        courseSearchDto.setPageNum(page);
        courseSearchDto.setSearch(search);

        courseSearchDto.setPageSize(6);
        
        // 검색
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

        List<LecturePreviewDto> lectures = lectureService.findAllByCourseId(course.getId());



        CourseDetailsDto courseDetailsDto = new CourseDetailsDto(course);

        // 모델 전달
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

        CourseUpdateDto courseUpdateDto = new CourseUpdateDto(course);

        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseUpdateDto", courseUpdateDto);

        return "/course/modifyForm";
    }

    // 강의 등록 요청
    @GetMapping("/lecturer/course/{courseId}/lecture/register")
    @Auth
    public String lectureRegisterForm(
            @PathVariable(name = "courseId") Long courseId,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){ return "redirect:/login";}

        // 코스 조회
        Course course = courseService.findById(courseId);
        if(course == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //인가
        if(member.getId() != course.getLecturer().getId()){throw new InvalidAccessException("작성 권한이 없습니다.");}

        // 데이터 전달
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseId", courseId);
        model.addAttribute("lectureRegisterDto", new LectureRegisterDto());

        return "/course/lecture/registerForm";
    }

    // 강의 관리 페이지 조회
    @GetMapping("/lecturer/course/{courseId}/{lectureId}")
    @Auth
    public String lectureDetailPage(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "lectureId") Long lectureId,
            Model model
    ){
        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        //강의 조회
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //인가
        if(lecture.getCourse().getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("접근 권한이 없습니다.");
        }

        // 데이터 전송
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("lecture", new LectureDetailsDto(lecture));

        return "/course/lecture/details";
    }

}
