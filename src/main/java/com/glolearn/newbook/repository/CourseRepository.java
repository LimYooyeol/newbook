package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.QCourse;
import com.glolearn.newbook.domain.QMember;
import com.glolearn.newbook.dto.course.CourseSearchDto;
import com.glolearn.newbook.dto.course.Sort;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.glolearn.newbook.domain.QCourse.course;
import static com.glolearn.newbook.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class CourseRepository {

    private final EntityManager em;


    public void save(Course course) {
        em.persist(course);
    }

    // 인기강의 조회
    public List<Course> findAllByOrderByNumStudentDesc(Pageable pageable){
        int start = pageable.getPageNumber()* pageable.getPageSize();

        return em.createQuery("select c from Course c " +
                " left join fetch c.lecturer " +
                " where c.isPublished = true " +
                " order by c.numStudent ")
                .setFirstResult(start)
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public Optional<Course> findById(Long courseId) {
        return Optional.ofNullable(em.find(Course.class, courseId));
    }

    public void deleteById(Long courseId) {
        em.remove(em.find(Course.class, courseId));
    }

    public List<Course> findCourses(CourseSearchDto courseSearchDto){
        JPAQuery<Course> query = new JPAQuery<>(em);

        QCourse course = QCourse.course;
        QMember member = QMember.member;

        return  query.from(course).join(course.lecturer, member).fetchJoin()
                .where(
                        isPublished(),
                        eqCategory(courseSearchDto.getCategory()),
                        titleLike(courseSearchDto.getSearch())
                )
                .orderBy(getSort(courseSearchDto.getSort()))
                .offset((courseSearchDto.getPageNum()-1)* courseSearchDto.getPageSize())
                .limit(courseSearchDto.getPageSize())
                .fetch();
    }

    public Long countCourses(CourseSearchDto courseSearchDto){
        JPAQuery<Course> query = new JPAQuery<>(em);

        QCourse course = QCourse.course;

        return  query.select(course.count()).from(course)
                .where(
                        isPublished(),
                        eqCategory(courseSearchDto.getCategory()),
                        titleLike(courseSearchDto.getSearch())
                )
                .orderBy(getSort(courseSearchDto.getSort()))
                .fetchOne();
    }

    private OrderSpecifier getSort(Sort sort) {
        if(sort == null){
            sort = Sort.RECENT;
        }

        if(sort.equals(Sort.RECENT)){
            return new OrderSpecifier(Order.DESC, course.regDate);
        }else if(sort.equals(Sort.POPULAR)){
            return new OrderSpecifier(Order.DESC, course.numStudent);
        }else{  //default
            return new OrderSpecifier(Order.DESC, course.regDate);
        }
    }

    private BooleanExpression isPublished() {
        return course.isPublished.eq(true);
    }

    private BooleanExpression titleLike(String search) {
        if(search == null){
            return null;
        }
        return course.title.like("%" + search + "%");
    }

    private BooleanExpression eqCategory(Category category){
        if(category == null){
            return  null;
        }
        return course.category.eq(category);
    }
}
