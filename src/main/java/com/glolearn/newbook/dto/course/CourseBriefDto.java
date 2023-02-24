package com.glolearn.newbook.dto.course;

import lombok.Data;

@Data
public class CourseBriefDto {
    private Long courseId;

    private String cover;

    private String title;

    private Long numStudent;

    private String nickname;
}
