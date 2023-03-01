package com.glolearn.newbook.domain;

import lombok.Getter;

import javax.persistence.*;
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
}
