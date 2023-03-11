package com.glolearn.newbook.domain;

import com.glolearn.newbook.dto.course.CourseRegisterDto;
import com.glolearn.newbook.dto.course.CourseUpdateDto;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@DynamicInsert
@DynamicUpdate
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer")
    private Member lecturer;

    private String title;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "introduction_id")
    private Introduction introduction;

    private LocalDateTime regDate;

    private Boolean isPublished;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String cover;

    private Long numStudent;

    protected Course(){}

    public static Course createCourse(Member lecturer, CourseRegisterDto courseRegisterDto){
        Course course = new Course();

        course.lecturer = lecturer;
        course.title = courseRegisterDto.getTitle();
        course.introduction = new Introduction(courseRegisterDto.getIntroduction());
        course.isPublished = courseRegisterDto.getIsPublished();
        course.category = courseRegisterDto.getCategory();
        course.cover = courseRegisterDto.getCover();

        return course;
    }

    public void update(CourseUpdateDto courseUpdateDto){
        this.title = courseUpdateDto.getTitle();
        this.introduction.updateIntroduction(courseUpdateDto.getIntroduction());
        this.isPublished = courseUpdateDto.getIsPublished();
        this.category = courseUpdateDto.getCategory();
        this.cover = courseUpdateDto.getCover();
    }

}
