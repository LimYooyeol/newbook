package com.glolearn.newbook.domain;

import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer")
    @NotNull
    private Member lecturer;

    @NotBlank
    @Size(min = 2, max = 100)
    private String title;

    private LocalDateTime regDate;

    private String introduction;

    private Boolean isPublished;

    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull
    private String cover;

    @NotNull
    private Long numStudent;

    public static Course createCourse(Member lecturer, String title, String introduction,
                               Boolean isPublished, Category category, String cover){
        Course course = new Course();
        course.lecturer = lecturer;
        course.title = title;
        course.regDate = LocalDateTime.now();
        course.isPublished = isPublished;
        course.introduction = introduction;
        course.category = category;
        course.cover = cover;

        return course;
    }
}
