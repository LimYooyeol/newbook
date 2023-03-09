package com.glolearn.newbook.dto.course;

import com.glolearn.newbook.domain.Course;
import lombok.Data;

import java.util.List;

@Data
public class CourseDetailsDto {
    Long id;

    String cover;

    String title;

    String lecturer;

    Long numStudent;

    String regDate;

    String introduction;

    public CourseDetailsDto(Course course){
        this.id = course.getId();
        this.cover = course.getCover();
        this.title = course.getTitle();
        this.lecturer = course.getLecturer().getNickname();
        this.numStudent = course.getNumStudent();
        this.regDate = course.getRegDate().toLocalDate().toString();
        this.introduction = course.getIntroduction().getIntroduction();
    }

//    List<LecturePreviewDto> lectures;
}
