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


    @Query("select c from Course c " +
            " left join fetch c.lecturer " +
            " left join fetch c.introduction " +
            " order by c.numStudent ")
    public List<Course> findAllByOrderByNumStudentDesc(Pageable pageable);
}
