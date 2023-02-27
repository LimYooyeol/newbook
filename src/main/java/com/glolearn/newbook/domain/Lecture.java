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

}
