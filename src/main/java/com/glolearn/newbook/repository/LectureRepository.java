package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureRepository {
    private final EntityManager em;

    public void save(Lecture lecture){
        em.persist(lecture);
    }

    public Lecture findById(Long lectureId){
        return em.find(Lecture.class, lectureId);
    }

    public List<Lecture> findAllByCourseId(Long courseId){
        return em.createQuery(
                "select l from Lecture l " +
                " where l.course.id =: courseId " +
                " order by l.id")
                .setParameter("courseId", courseId)
                .getResultList();
    }

    public void delete(Lecture lecture){
        em.remove(lecture);
    }
}
