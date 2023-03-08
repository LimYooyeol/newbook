package com.glolearn.newbook.dto.course;

import com.glolearn.newbook.domain.Category;
import lombok.Data;

@Data
public class CourseSearchDto {
    private Category category;

    private String search;

    private int pageNum;

    private int pageSize;

    private Sort sort;
}
