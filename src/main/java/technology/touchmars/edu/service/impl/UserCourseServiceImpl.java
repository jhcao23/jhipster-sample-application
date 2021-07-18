package technology.touchmars.edu.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technology.touchmars.edu.domain.UserCourse;
import technology.touchmars.edu.repository.UserCourseRepository;
import technology.touchmars.edu.service.UserCourseService;

/**
 * Service Implementation for managing {@link UserCourse}.
 */
@Service
@Transactional
public class UserCourseServiceImpl implements UserCourseService {

    private final Logger log = LoggerFactory.getLogger(UserCourseServiceImpl.class);

    private final UserCourseRepository userCourseRepository;

    public UserCourseServiceImpl(UserCourseRepository userCourseRepository) {
        this.userCourseRepository = userCourseRepository;
    }

    @Override
    public UserCourse save(UserCourse userCourse) {
        log.debug("Request to save UserCourse : {}", userCourse);
        return userCourseRepository.save(userCourse);
    }

    @Override
    public Optional<UserCourse> partialUpdate(UserCourse userCourse) {
        log.debug("Request to partially update UserCourse : {}", userCourse);

        return userCourseRepository
            .findById(userCourse.getId())
            .map(
                existingUserCourse -> {
                    if (userCourse.getCode() != null) {
                        existingUserCourse.setCode(userCourse.getCode());
                    }
                    if (userCourse.getCourseType() != null) {
                        existingUserCourse.setCourseType(userCourse.getCourseType());
                    }
                    if (userCourse.getName() != null) {
                        existingUserCourse.setName(userCourse.getName());
                    }
                    if (userCourse.getDesc() != null) {
                        existingUserCourse.setDesc(userCourse.getDesc());
                    }
                    if (userCourse.getUrl() != null) {
                        existingUserCourse.setUrl(userCourse.getUrl());
                    }
                    if (userCourse.getCover() != null) {
                        existingUserCourse.setCover(userCourse.getCover());
                    }
                    if (userCourse.getCoverContentType() != null) {
                        existingUserCourse.setCoverContentType(userCourse.getCoverContentType());
                    }
                    if (userCourse.getBeginDt() != null) {
                        existingUserCourse.setBeginDt(userCourse.getBeginDt());
                    }
                    if (userCourse.getDueDt() != null) {
                        existingUserCourse.setDueDt(userCourse.getDueDt());
                    }
                    if (userCourse.getUserId() != null) {
                        existingUserCourse.setUserId(userCourse.getUserId());
                    }

                    return existingUserCourse;
                }
            )
            .map(userCourseRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserCourse> findAll(Pageable pageable) {
        log.debug("Request to get all UserCourses");
        return userCourseRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserCourse> findOne(Long id) {
        log.debug("Request to get UserCourse : {}", id);
        return userCourseRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserCourse : {}", id);
        userCourseRepository.deleteById(id);
    }
}
