package technology.touchmars.edu.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import technology.touchmars.edu.domain.UserCourse;

/**
 * Spring Data SQL repository for the UserCourse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long>, JpaSpecificationExecutor<UserCourse> {}
