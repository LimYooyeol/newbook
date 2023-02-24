package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.dto.course.CourseBriefDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query(value = "SELECT C.course_id, C.cover, C.title, C.num_student, M.nickname\n" +
            "FROM COURSE C JOIN MEMBERS M ON C.lecturer = M.member_id\n" +
            "ORDER BY C.num_student DESC",
            nativeQuery = true)
    public List<CourseBriefDto> findCourseBriefByNumStudentDesc(Pageable pageable);
}
