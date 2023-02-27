package com.glolearn.newbook.controller;


import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.context.UserContext;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.service.CourseService;
import com.glolearn.newbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberService memberService;
    private final CourseService courseService;

    @GetMapping("/")
    @Auth
    public String home(Model model){
        // token 에 값이 아직 있더라도, 서버에서 제거된 경우 (탈퇴처리 등)를 고려하여 DB 에서 조회
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member != null){
            model.addAttribute("nickname", member.getNickname());
        }



        return "index";
    }
}
