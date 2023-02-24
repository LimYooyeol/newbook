package com.glolearn.newbook.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private Boolean published;      // [0: false, 1: true]

    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull
    private String cover;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Enrollment> enrollmentList = new ArrayList<>();

    // mappedBy 에 들어가는 값은 실제 Lecture class 의 변수 이름
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Lecture> lectureList = new ArrayList<>();

    @NotNull
    private Long numStudent;

    public static Course createCourse(Member lecturer, String title, String introduction,
                               Boolean published, Category category, String cover){
        Course course = new Course();
        course.lecturer = lecturer;
        course.title = title;
        course.regDate = LocalDateTime.now();
        course.published = published;
        course.introduction = introduction;
        course.category = category;
        course.cover = cover;

        return course;
    }
}
