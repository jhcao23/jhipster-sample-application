package technology.touchmars.edu.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.UserCourse;

/**
 * Spring Data SQL reactive repository for the UserCourse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserCourseRepository extends R2dbcRepository<UserCourse, Long>, UserCourseRepositoryInternal {
    Flux<UserCourse> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<UserCourse> findAll();

    @Override
    Mono<UserCourse> findById(Long id);

    @Override
    <S extends UserCourse> Mono<S> save(S entity);
}

interface UserCourseRepositoryInternal {
    <S extends UserCourse> Mono<S> insert(S entity);
    <S extends UserCourse> Mono<S> save(S entity);
    Mono<Integer> update(UserCourse entity);

    Flux<UserCourse> findAll();
    Mono<UserCourse> findById(Long id);
    Flux<UserCourse> findAllBy(Pageable pageable);
    Flux<UserCourse> findAllBy(Pageable pageable, Criteria criteria);
}
