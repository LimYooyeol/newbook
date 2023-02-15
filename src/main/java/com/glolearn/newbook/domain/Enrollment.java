package com.glolearn.newbook.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Enrollment {
    @Id
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDateTime enrollDate;

}
