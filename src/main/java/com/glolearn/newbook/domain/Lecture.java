package com.glolearn.newbook.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "lecture_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer")
    private Member lecturer;

    @Size(min = 2, max = 50)
    private String title;

    private LocalDateTime regDate;

    private LocalDateTime lastUpdateDate;

    @NotNull
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")         // 여기서 name: Lecture table 에서 column name, Course 와는 상관없음
    private Course course;                  // join 은 어차피 course table 의 PK를 대상으로 이뤄짐

    // 편의 메서드, 한쪽에만 작성하는 것으로 선택
    // 양쪽에 작성하면 무한 루프 주의해야 됨
    public void setCourse(Course course){
        if(this.course != null){    // 혹시라도 강의가 이동되는 경우
            this.course.getLectures().remove(this);
        }

        this.course = course;
        course.getLectures().add(this);
    }
}
