package technology.touchmars.edu.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.Course;

/**
 * Spring Data SQL reactive repository for the Course entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseRepository extends R2dbcRepository<Course, Long>, CourseRepositoryInternal {
    Flux<Course> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Course> findAll();

    @Override
    Mono<Course> findById(Long id);

    @Override
    <S extends Course> Mono<S> save(S entity);
}

interface CourseRepositoryInternal {
    <S extends Course> Mono<S> insert(S entity);
    <S extends Course> Mono<S> save(S entity);
    Mono<Integer> update(Course entity);

    Flux<Course> findAll();
    Mono<Course> findById(Long id);
    Flux<Course> findAllBy(Pageable pageable);
    Flux<Course> findAllBy(Pageable pageable, Criteria criteria);
}
