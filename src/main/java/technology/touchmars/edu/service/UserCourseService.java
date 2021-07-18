package technology.touchmars.edu.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.UserCourse;

/**
 * Service Interface for managing {@link UserCourse}.
 */
public interface UserCourseService {
    /**
     * Save a userCourse.
     *
     * @param userCourse the entity to save.
     * @return the persisted entity.
     */
    Mono<UserCourse> save(UserCourse userCourse);

    /**
     * Partially updates a userCourse.
     *
     * @param userCourse the entity to update partially.
     * @return the persisted entity.
     */
    Mono<UserCourse> partialUpdate(UserCourse userCourse);

    /**
     * Get all the userCourses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<UserCourse> findAll(Pageable pageable);

    /**
     * Returns the number of userCourses available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" userCourse.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<UserCourse> findOne(Long id);

    /**
     * Delete the "id" userCourse.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
