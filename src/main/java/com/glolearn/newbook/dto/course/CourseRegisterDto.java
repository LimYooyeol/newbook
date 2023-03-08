package com.glolearn.newbook.dto.course;

import com.glolearn.newbook.domain.Category;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CourseRegisterDto {

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
}
