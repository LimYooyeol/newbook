package com.glolearn.newbook.domain;

import com.glolearn.newbook.dto.lecture.LectureRegisterDto;
import com.glolearn.newbook.dto.lecture.LectureUpdateDto;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")         // 여기서 name: Lecture table 에서 column name, Course 와는 상관없음
    private Course course;                  // join 은 어차피 course table 의 PK를 대상으로 이뤄짐

    private String title;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "contents_id")
    private Contents contents;

    private LocalDateTime regDate;

    private LocalDateTime lastUpdateDate;


    public static Lecture createLecture(Course course, LectureRegisterDto lectureRegisterDto) {
        Lecture lecture = new Lecture();

        lecture.course = course;
        lecture.title = lectureRegisterDto.getTitle();
        lecture.contents = new Contents(lectureRegisterDto.getContents());

        return lecture;
    }

    public void update(LectureUpdateDto lectureUpdateDto) {
        this.title = lectureUpdateDto.getTitle();
        this.contents.updateContents(lectureUpdateDto.getContents());
    }
}
