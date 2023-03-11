package com.glolearn.newbook.dto.lecture;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LectureRegisterDto {

    @NotBlank
    @Size(min = 2, max = 30)
    private String title;

    @NotBlank
    private String contents;
}
