package technology.touchmars.edu.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.Interest;
import technology.touchmars.edu.repository.InterestRepository;
import technology.touchmars.edu.service.InterestService;

/**
 * Service Implementation for managing {@link Interest}.
 */
@Service
@Transactional
public class InterestServiceImpl implements InterestService {

    private final Logger log = LoggerFactory.getLogger(InterestServiceImpl.class);

    private final InterestRepository interestRepository;

    public InterestServiceImpl(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    @Override
    public Mono<Interest> save(Interest interest) {
        log.debug("Request to save Interest : {}", interest);
        return interestRepository.save(interest);
    }

    @Override
    public Mono<Interest> partialUpdate(Interest interest) {
        log.debug("Request to partially update Interest : {}", interest);

        return interestRepository
            .findById(interest.getId())
            .map(existingInterest -> {
                if (interest.getName() != null) {
                    existingInterest.setName(interest.getName());
                }
                if (interest.getCode() != null) {
                    existingInterest.setCode(interest.getCode());
                }

                return existingInterest;
            })
            .flatMap(interestRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Interest> findAll(Pageable pageable) {
        log.debug("Request to get all Interests");
        return interestRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return interestRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Interest> findOne(Long id) {
        log.debug("Request to get Interest : {}", id);
        return interestRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Interest : {}", id);
        return interestRepository.deleteById(id);
    }
}
