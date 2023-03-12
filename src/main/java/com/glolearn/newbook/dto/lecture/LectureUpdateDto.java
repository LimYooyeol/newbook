package com.glolearn.newbook.dto.lecture;

import com.glolearn.newbook.domain.Lecture;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LectureUpdateDto {

    @Size(min = 2, max = 50)
    private String title;

    @NotBlank
    private String contents;

    public LectureUpdateDto(){}

    public LectureUpdateDto(Lecture lecture) {
        this.title = lecture.getTitle();
        this.contents = lecture.getContents().getContents();
    }
}
