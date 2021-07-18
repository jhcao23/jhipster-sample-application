package technology.touchmars.edu.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technology.touchmars.edu.domain.UserProfile;
import technology.touchmars.edu.repository.UserProfileRepository;
import technology.touchmars.edu.service.UserProfileService;

/**
 * Service Implementation for managing {@link UserProfile}.
 */
@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final Logger log = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository userProfileRepository;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile save(UserProfile userProfile) {
        log.debug("Request to save UserProfile : {}", userProfile);
        return userProfileRepository.save(userProfile);
    }

    @Override
    public Optional<UserProfile> partialUpdate(UserProfile userProfile) {
        log.debug("Request to partially update UserProfile : {}", userProfile);

        return userProfileRepository
            .findById(userProfile.getId())
            .map(
                existingUserProfile -> {
                    if (userProfile.getName() != null) {
                        existingUserProfile.setName(userProfile.getName());
                    }
                    if (userProfile.getAvatar() != null) {
                        existingUserProfile.setAvatar(userProfile.getAvatar());
                    }
                    if (userProfile.getAvatarContentType() != null) {
                        existingUserProfile.setAvatarContentType(userProfile.getAvatarContentType());
                    }
                    if (userProfile.getInterests() != null) {
                        existingUserProfile.setInterests(userProfile.getInterests());
                    }
                    if (userProfile.getUserId() != null) {
                        existingUserProfile.setUserId(userProfile.getUserId());
                    }

                    return existingUserProfile;
                }
            )
            .map(userProfileRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfile> findAll(Pageable pageable) {
        log.debug("Request to get all UserProfiles");
        return userProfileRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfile> findOne(Long id) {
        log.debug("Request to get UserProfile : {}", id);
        return userProfileRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserProfile : {}", id);
        userProfileRepository.deleteById(id);
    }
}
