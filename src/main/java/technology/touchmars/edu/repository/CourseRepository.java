package technology.touchmars.edu.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import technology.touchmars.edu.domain.Course;

/**
 * Spring Data SQL repository for the Course entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {}
