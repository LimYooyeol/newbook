package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.dto.lecture.LectureRegisterDto;
import com.glolearn.newbook.dto.lecture.LectureUpdateDto;
import com.glolearn.newbook.repository.CourseRepository;
import com.glolearn.newbook.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;

    //강의 추가
    @Transactional
    public Long addLecture(Long courseId, LectureRegisterDto lectureRegisterDto){
        Course course = courseRepository.findById(courseId).orElse(null);

        Lecture lecture = Lecture.createLecture(course, lectureRegisterDto);

        lectureRepository.save(lecture);
        return lecture.getId();
    }

    //강의 조회
    public Lecture findById(Long lectureId){
        return lectureRepository.findById(lectureId).orElse(null);
    }

    //강의 삭제
    public void removeById(Long lectureId){
        lectureRepository.deleteById(lectureId);
    }

    //강의 업데이트
    @Transactional
    public void modifyLecture(Long lectureId, LectureUpdateDto lectureUpdateDto){
        Lecture lecture = lectureRepository.findById(lectureId).orElse(null);
        lecture.update(lectureUpdateDto);
    }

}
