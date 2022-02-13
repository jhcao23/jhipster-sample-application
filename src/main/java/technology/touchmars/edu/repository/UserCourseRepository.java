package technology.touchmars.edu.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.UserCourse;

/**
 * Spring Data SQL reactive repository for the UserCourse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserCourseRepository extends ReactiveCrudRepository<UserCourse, Long>, UserCourseRepositoryInternal {
    Flux<UserCourse> findAllBy(Pageable pageable);

    @Query("SELECT * FROM user_course entity WHERE entity.user_id = :id")
    Flux<UserCourse> findByUser(Long id);

    @Query("SELECT * FROM user_course entity WHERE entity.user_id IS NULL")
    Flux<UserCourse> findAllWhereUserIsNull();

    @Query("SELECT * FROM user_course entity WHERE entity.course_id = :id")
    Flux<UserCourse> findByCourse(Long id);

    @Query("SELECT * FROM user_course entity WHERE entity.course_id IS NULL")
    Flux<UserCourse> findAllWhereCourseIsNull();

    @Override
    <S extends UserCourse> Mono<S> save(S entity);

    @Override
    Flux<UserCourse> findAll();

    @Override
    Mono<UserCourse> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UserCourseRepositoryInternal {
    <S extends UserCourse> Mono<S> save(S entity);

    Flux<UserCourse> findAllBy(Pageable pageable);

    Flux<UserCourse> findAll();

    Mono<UserCourse> findById(Long id);

    Flux<UserCourse> findAllBy(Pageable pageable, Criteria criteria);
}
