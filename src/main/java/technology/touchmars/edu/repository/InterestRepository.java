package technology.touchmars.edu.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.Interest;

/**
 * Spring Data SQL reactive repository for the Interest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InterestRepository extends ReactiveCrudRepository<Interest, Long>, InterestRepositoryInternal {
    Flux<Interest> findAllBy(Pageable pageable);

    @Override
    <S extends Interest> Mono<S> save(S entity);

    @Override
    Flux<Interest> findAll();

    @Override
    Mono<Interest> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InterestRepositoryInternal {
    <S extends Interest> Mono<S> save(S entity);

    Flux<Interest> findAllBy(Pageable pageable);

    Flux<Interest> findAll();

    Mono<Interest> findById(Long id);

    Flux<Interest> findAllBy(Pageable pageable, Criteria criteria);
}
