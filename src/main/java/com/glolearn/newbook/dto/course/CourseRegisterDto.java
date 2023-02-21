package com.glolearn.newbook.dto.course;

import com.glolearn.newbook.domain.Category;
import lombok.Data;

@Data
public class CourseRegisterDto {
    private String cover;

    private String courseName;

    private Boolean published;

    private String introduction;

    private Category category;
}
