package technology.touchmars.edu.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    UserProfile save(UserProfile userProfile);

    /**
     * Partially updates a userProfile.
     *
     * @param userProfile the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserProfile> partialUpdate(UserProfile userProfile);

    /**
     * Get all the userProfiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserProfile> findAll(Pageable pageable);

    /**
     * Get the "id" userProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserProfile> findOne(Long id);

    /**
     * Delete the "id" userProfile.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
