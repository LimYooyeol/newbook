package com.glolearn.newbook.dto.course;

import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CourseUpdateDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 30)
    private String title;

    @NotBlank
    private String introduction;

    @NotNull
    private Boolean isPublished;

    @NotNull
    private Category category;

    @NotBlank
    private String cover;

    public CourseUpdateDto(){}

    public CourseUpdateDto(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.introduction = course.getIntroduction().getIntroduction();
        this.isPublished = course.getIsPublished();
        this.category = course.getCategory();
        this.cover = course.getCover();
    }
}
