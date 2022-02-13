package technology.touchmars.edu.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.Course;

/**
 * Spring Data SQL reactive repository for the Course entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseRepository extends ReactiveCrudRepository<Course, Long>, CourseRepositoryInternal {
    Flux<Course> findAllBy(Pageable pageable);

    @Override
    <S extends Course> Mono<S> save(S entity);

    @Override
    Flux<Course> findAll();

    @Override
    Mono<Course> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CourseRepositoryInternal {
    <S extends Course> Mono<S> save(S entity);

    Flux<Course> findAllBy(Pageable pageable);

    Flux<Course> findAll();

    Mono<Course> findById(Long id);

    Flux<Course> findAllBy(Pageable pageable, Criteria criteria);
}
