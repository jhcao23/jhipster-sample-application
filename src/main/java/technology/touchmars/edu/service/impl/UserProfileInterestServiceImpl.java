package technology.touchmars.edu.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.UserProfileInterest;
import technology.touchmars.edu.repository.UserProfileInterestRepository;
import technology.touchmars.edu.service.UserProfileInterestService;

/**
 * Service Implementation for managing {@link UserProfileInterest}.
 */
@Service
@Transactional
public class UserProfileInterestServiceImpl implements UserProfileInterestService {

    private final Logger log = LoggerFactory.getLogger(UserProfileInterestServiceImpl.class);

    private final UserProfileInterestRepository userProfileInterestRepository;

    public UserProfileInterestServiceImpl(UserProfileInterestRepository userProfileInterestRepository) {
        this.userProfileInterestRepository = userProfileInterestRepository;
    }

    @Override
    public Mono<UserProfileInterest> save(UserProfileInterest userProfileInterest) {
        log.debug("Request to save UserProfileInterest : {}", userProfileInterest);
        return userProfileInterestRepository.save(userProfileInterest);
    }

    @Override
    public Mono<UserProfileInterest> partialUpdate(UserProfileInterest userProfileInterest) {
        log.debug("Request to partially update UserProfileInterest : {}", userProfileInterest);

        return userProfileInterestRepository
            .findById(userProfileInterest.getId())
            .map(existingUserProfileInterest -> {
                if (userProfileInterest.getCode() != null) {
                    existingUserProfileInterest.setCode(userProfileInterest.getCode());
                }

                return existingUserProfileInterest;
            })
            .flatMap(userProfileInterestRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserProfileInterest> findAll(Pageable pageable) {
        log.debug("Request to get all UserProfileInterests");
        return userProfileInterestRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return userProfileInterestRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserProfileInterest> findOne(Long id) {
        log.debug("Request to get UserProfileInterest : {}", id);
        return userProfileInterestRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete UserProfileInterest : {}", id);
        return userProfileInterestRepository.deleteById(id);
    }
}
