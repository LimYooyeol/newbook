package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final MemberService memberService;

    @GetMapping("/image/{memberId}/{filename}")
    public void getImage(
            @PathVariable(name = "memberId") String memberId,
            @PathVariable(name = "filename") String filename,
            HttpServletResponse response
    ) throws IOException {
        File image = new File("C:/Users/duf70/OneDrive/바탕 화면/glo_image/" + memberId + "/" + filename);

        FileInputStream fileInputStream = new FileInputStream(image);
        IOUtils.copy(fileInputStream, response.getOutputStream());
        fileInputStream.close();
    }

    @PostMapping("/image")
    @ResponseBody
    @Auth
    public String temp(MultipartFile image) throws IOException {
        if(UserContext.getCurrentMember() == null){
            throw new InvalidAccessException("Not authenticated");
        }
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){ throw new InvalidAccessException("Non-existing member.");}

        File base = new File("C:/Users/duf70/OneDrive/바탕 화면/glo_image/" + member.getId());
        if(!base.exists()) {base.mkdir();}

        File file = new File(base + "/" + UUID.randomUUID() + ".png");

        if(!file.exists()) {file.mkdir();}
        image.transferTo(file);

        JSONObject response = new JSONObject();
        response.put("url", "/image/" + member.getId() + "/" + file.getName());

        return response.toString();
    }
}
