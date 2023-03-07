package com.glolearn.newbook.dto.course;

import com.glolearn.newbook.domain.Category;
import lombok.Data;

@Data
public class CourseUpdateDto {
    private String title;

    private String introduction;

    private Boolean isPublished;

    private Category category;

    private String cover;
}
