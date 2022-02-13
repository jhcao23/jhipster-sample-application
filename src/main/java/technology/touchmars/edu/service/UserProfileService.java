package technology.touchmars.edu.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.UserProfile;

/**
 * Service Interface for managing {@link UserProfile}.
 */
public interface UserProfileService {
    /**
     * Save a userProfile.
     *
     * @param userProfile the entity to save.
     * @return the persisted entity.
     */
    Mono<UserProfile> save(UserProfile userProfile);

    /**
     * Partially updates a userProfile.
     *
     * @param userProfile the entity to update partially.
     * @return the persisted entity.
     */
    Mono<UserProfile> partialUpdate(UserProfile userProfile);

    /**
     * Get all the userProfiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<UserProfile> findAll(Pageable pageable);

    /**
     * Returns the number of userProfiles available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" userProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<UserProfile> findOne(Long id);

    /**
     * Delete the "id" userProfile.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
