package technology.touchmars.edu.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import technology.touchmars.edu.domain.UserProfile;

/**
 * Spring Data SQL repository for the UserProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>, JpaSpecificationExecutor<UserProfile> {}
