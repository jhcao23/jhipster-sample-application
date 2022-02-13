package technology.touchmars.edu.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.Interest;

/**
 * Service Interface for managing {@link Interest}.
 */
public interface InterestService {
    /**
     * Save a interest.
     *
     * @param interest the entity to save.
     * @return the persisted entity.
     */
    Mono<Interest> save(Interest interest);

    /**
     * Partially updates a interest.
     *
     * @param interest the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Interest> partialUpdate(Interest interest);

    /**
     * Get all the interests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Interest> findAll(Pageable pageable);

    /**
     * Returns the number of interests available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" interest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Interest> findOne(Long id);

    /**
     * Delete the "id" interest.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
