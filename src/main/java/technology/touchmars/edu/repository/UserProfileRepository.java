package technology.touchmars.edu.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.UserProfile;

/**
 * Spring Data SQL reactive repository for the UserProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserProfileRepository extends R2dbcRepository<UserProfile, Long>, UserProfileRepositoryInternal {
    Flux<UserProfile> findAllBy(Pageable pageable);

    @Query("SELECT * FROM user_profile entity WHERE entity.user_id = :id")
    Flux<UserProfile> findByUser(Long id);

    @Query("SELECT * FROM user_profile entity WHERE entity.user_id IS NULL")
    Flux<UserProfile> findAllWhereUserIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<UserProfile> findAll();

    @Override
    Mono<UserProfile> findById(Long id);

    @Override
    <S extends UserProfile> Mono<S> save(S entity);
}

interface UserProfileRepositoryInternal {
    <S extends UserProfile> Mono<S> insert(S entity);
    <S extends UserProfile> Mono<S> save(S entity);
    Mono<Integer> update(UserProfile entity);

    Flux<UserProfile> findAll();
    Mono<UserProfile> findById(Long id);
    Flux<UserProfile> findAllBy(Pageable pageable);
    Flux<UserProfile> findAllBy(Pageable pageable, Criteria criteria);
}
