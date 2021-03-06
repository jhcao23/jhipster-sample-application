package technology.touchmars.edu.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
    public Mono<UserCourse> save(UserCourse userCourse) {
        log.debug("Request to save UserCourse : {}", userCourse);
        return userCourseRepository.save(userCourse);
    }

    @Override
    public Mono<UserCourse> partialUpdate(UserCourse userCourse) {
        log.debug("Request to partially update UserCourse : {}", userCourse);

        return userCourseRepository
            .findById(userCourse.getId())
            .map(existingUserCourse -> {
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

                return existingUserCourse;
            })
            .flatMap(userCourseRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserCourse> findAll(Pageable pageable) {
        log.debug("Request to get all UserCourses");
        return userCourseRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return userCourseRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserCourse> findOne(Long id) {
        log.debug("Request to get UserCourse : {}", id);
        return userCourseRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete UserCourse : {}", id);
        return userCourseRepository.deleteById(id);
    }
}
