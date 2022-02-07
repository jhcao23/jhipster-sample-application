package technology.touchmars.edu.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.UserProfileInterest;

/**
 * Spring Data SQL reactive repository for the UserProfileInterest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserProfileInterestRepository
    extends ReactiveCrudRepository<UserProfileInterest, Long>, UserProfileInterestRepositoryInternal {
    Flux<UserProfileInterest> findAllBy(Pageable pageable);

    @Query("SELECT * FROM user_profile_interest entity WHERE entity.user_profile_id = :id")
    Flux<UserProfileInterest> findByUserProfile(Long id);

    @Query("SELECT * FROM user_profile_interest entity WHERE entity.user_profile_id IS NULL")
    Flux<UserProfileInterest> findAllWhereUserProfileIsNull();

    @Query("SELECT * FROM user_profile_interest entity WHERE entity.interest_id = :id")
    Flux<UserProfileInterest> findByInterest(Long id);

    @Query("SELECT * FROM user_profile_interest entity WHERE entity.interest_id IS NULL")
    Flux<UserProfileInterest> findAllWhereInterestIsNull();

    @Override
    <S extends UserProfileInterest> Mono<S> save(S entity);

    @Override
    Flux<UserProfileInterest> findAll();

    @Override
    Mono<UserProfileInterest> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UserProfileInterestRepositoryInternal {
    <S extends UserProfileInterest> Mono<S> save(S entity);

    Flux<UserProfileInterest> findAllBy(Pageable pageable);

    Flux<UserProfileInterest> findAll();

    Mono<UserProfileInterest> findById(Long id);

    Flux<UserProfileInterest> findAllBy(Pageable pageable, Criteria criteria);
}
