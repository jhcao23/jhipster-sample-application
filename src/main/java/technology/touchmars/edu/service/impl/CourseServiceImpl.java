package technology.touchmars.edu.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technology.touchmars.edu.domain.Course;
import technology.touchmars.edu.repository.CourseRepository;
import technology.touchmars.edu.service.CourseService;

/**
 * Service Implementation for managing {@link Course}.
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Course save(Course course) {
        log.debug("Request to save Course : {}", course);
        return courseRepository.save(course);
    }

    @Override
    public Optional<Course> partialUpdate(Course course) {
        log.debug("Request to partially update Course : {}", course);

        return courseRepository
            .findById(course.getId())
            .map(
                existingCourse -> {
                    if (course.getCode() != null) {
                        existingCourse.setCode(course.getCode());
                    }
                    if (course.getCourseType() != null) {
                        existingCourse.setCourseType(course.getCourseType());
                    }
                    if (course.getName() != null) {
                        existingCourse.setName(course.getName());
                    }
                    if (course.getDesc() != null) {
                        existingCourse.setDesc(course.getDesc());
                    }
                    if (course.getUrl() != null) {
                        existingCourse.setUrl(course.getUrl());
                    }
                    if (course.getCover() != null) {
                        existingCourse.setCover(course.getCover());
                    }
                    if (course.getCoverContentType() != null) {
                        existingCourse.setCoverContentType(course.getCoverContentType());
                    }
                    if (course.getVersion() != null) {
                        existingCourse.setVersion(course.getVersion());
                    }
                    if (course.getCreatedDt() != null) {
                        existingCourse.setCreatedDt(course.getCreatedDt());
                    }
                    if (course.getCreatedBy() != null) {
                        existingCourse.setCreatedBy(course.getCreatedBy());
                    }
                    if (course.getStartDt() != null) {
                        existingCourse.setStartDt(course.getStartDt());
                    }
                    if (course.getEndDt() != null) {
                        existingCourse.setEndDt(course.getEndDt());
                    }

                    return existingCourse;
                }
            )
            .map(courseRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Course> findAll(Pageable pageable) {
        log.debug("Request to get all Courses");
        return courseRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findOne(Long id) {
        log.debug("Request to get Course : {}", id);
        return courseRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Course : {}", id);
        courseRepository.deleteById(id);
    }
}
