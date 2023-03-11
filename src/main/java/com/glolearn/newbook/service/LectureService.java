package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.dto.lecture.LecturePreviewDto;
import com.glolearn.newbook.dto.lecture.LectureRegisterDto;
import com.glolearn.newbook.dto.lecture.LectureUpdateDto;
import com.glolearn.newbook.repository.CourseRepository;
import com.glolearn.newbook.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if(course == null) { throw new IllegalArgumentException("존재하지 않는 코스입니다.");}

        Lecture lecture = Lecture.createLecture(course, lectureRegisterDto);
        lectureRepository.save(lecture);

        return lecture.getId();
    }

    //강의 조회
    public Lecture findById(Long lectureId){
        return lectureRepository.findById(lectureId);
    }

    public List<LecturePreviewDto> findAllByCourseId(Long courseId){
        List<Lecture> lectures = lectureRepository.findAllByCourseId(courseId);

        List<LecturePreviewDto> result = lectures.stream()
                .map(l -> new LecturePreviewDto(l))
                .collect(Collectors.toList());

        return result;
    }


    //강의 삭제
    @Transactional
    public void removeById(Long lectureId){
        Lecture lecture = lectureRepository.findById(lectureId);
        if(lecture == null) {throw new IllegalArgumentException("존재하지 않는 강의입니다.");}

        lectureRepository.delete(lecture);
    }

    //강의 업데이트
    @Transactional
    public void modifyLecture(Long lectureId, LectureUpdateDto lectureUpdateDto){
        Lecture lecture = lectureRepository.findById(lectureId);
        if(lecture == null){throw new IllegalArgumentException("존재하지 않는 강의입니다.");}

        lecture.update(lectureUpdateDto);
    }

}
