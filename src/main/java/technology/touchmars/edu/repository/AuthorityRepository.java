package technology.touchmars.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technology.touchmars.edu.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
