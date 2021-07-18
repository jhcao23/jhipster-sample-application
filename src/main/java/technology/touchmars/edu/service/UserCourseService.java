package technology.touchmars.edu.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    UserCourse save(UserCourse userCourse);

    /**
     * Partially updates a userCourse.
     *
     * @param userCourse the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserCourse> partialUpdate(UserCourse userCourse);

    /**
     * Get all the userCourses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserCourse> findAll(Pageable pageable);

    /**
     * Get the "id" userCourse.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserCourse> findOne(Long id);

    /**
     * Delete the "id" userCourse.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
