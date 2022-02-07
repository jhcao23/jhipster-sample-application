package technology.touchmars.edu.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.Course;

/**
 * Service Interface for managing {@link Course}.
 */
public interface CourseService {
    /**
     * Save a course.
     *
     * @param course the entity to save.
     * @return the persisted entity.
     */
    Mono<Course> save(Course course);

    /**
     * Partially updates a course.
     *
     * @param course the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Course> partialUpdate(Course course);

    /**
     * Get all the courses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Course> findAll(Pageable pageable);

    /**
     * Returns the number of courses available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" course.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Course> findOne(Long id);

    /**
     * Delete the "id" course.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
