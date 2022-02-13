package technology.touchmars.edu.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.UserProfileInterest;

/**
 * Service Interface for managing {@link UserProfileInterest}.
 */
public interface UserProfileInterestService {
    /**
     * Save a userProfileInterest.
     *
     * @param userProfileInterest the entity to save.
     * @return the persisted entity.
     */
    Mono<UserProfileInterest> save(UserProfileInterest userProfileInterest);

    /**
     * Partially updates a userProfileInterest.
     *
     * @param userProfileInterest the entity to update partially.
     * @return the persisted entity.
     */
    Mono<UserProfileInterest> partialUpdate(UserProfileInterest userProfileInterest);

    /**
     * Get all the userProfileInterests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<UserProfileInterest> findAll(Pageable pageable);

    /**
     * Returns the number of userProfileInterests available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" userProfileInterest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<UserProfileInterest> findOne(Long id);

    /**
     * Delete the "id" userProfileInterest.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
