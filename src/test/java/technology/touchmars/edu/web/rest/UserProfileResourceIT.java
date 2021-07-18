package technology.touchmars.edu.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import technology.touchmars.edu.IntegrationTest;
import technology.touchmars.edu.domain.User;
import technology.touchmars.edu.domain.UserProfile;
import technology.touchmars.edu.repository.UserProfileRepository;
import technology.touchmars.edu.service.EntityManager;

/**
 * Integration tests for the {@link UserProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class UserProfileResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_AVATAR = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_AVATAR = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_AVATAR_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_AVATAR_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_INTERESTS = "AAAAAAAAAA";
    private static final String UPDATED_INTERESTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserProfile userProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProfile createEntity(EntityManager em) {
        UserProfile userProfile = new UserProfile()
            .name(DEFAULT_NAME)
            .avatar(DEFAULT_AVATAR)
            .avatarContentType(DEFAULT_AVATAR_CONTENT_TYPE)
            .interests(DEFAULT_INTERESTS);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        userProfile.setUser(user);
        return userProfile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProfile createUpdatedEntity(EntityManager em) {
        UserProfile userProfile = new UserProfile()
            .name(UPDATED_NAME)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .interests(UPDATED_INTERESTS);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        userProfile.setUser(user);
        return userProfile;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserProfile.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        userProfile = createEntity(em);
    }

    @Test
    void createUserProfile() throws Exception {
        int databaseSizeBeforeCreate = userProfileRepository.findAll().collectList().block().size();
        // Create the UserProfile
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfile))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeCreate + 1);
        UserProfile testUserProfile = userProfileList.get(userProfileList.size() - 1);
        assertThat(testUserProfile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserProfile.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testUserProfile.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
        assertThat(testUserProfile.getInterests()).isEqualTo(DEFAULT_INTERESTS);
    }

    @Test
    void createUserProfileWithExistingId() throws Exception {
        // Create the UserProfile with an existing ID
        userProfile.setId(1L);

        int databaseSizeBeforeCreate = userProfileRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllUserProfiles() {
        // Initialize the database
        userProfileRepository.save(userProfile).block();

        // Get all the userProfileList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(userProfile.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].avatarContentType")
            .value(hasItem(DEFAULT_AVATAR_CONTENT_TYPE))
            .jsonPath("$.[*].avatar")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_AVATAR)))
            .jsonPath("$.[*].interests")
            .value(hasItem(DEFAULT_INTERESTS));
    }

    @Test
    void getUserProfile() {
        // Initialize the database
        userProfileRepository.save(userProfile).block();

        // Get the userProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userProfile.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.avatarContentType")
            .value(is(DEFAULT_AVATAR_CONTENT_TYPE))
            .jsonPath("$.avatar")
            .value(is(Base64Utils.encodeToString(DEFAULT_AVATAR)))
            .jsonPath("$.interests")
            .value(is(DEFAULT_INTERESTS));
    }

    @Test
    void getNonExistingUserProfile() {
        // Get the userProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUserProfile() throws Exception {
        // Initialize the database
        userProfileRepository.save(userProfile).block();

        int databaseSizeBeforeUpdate = userProfileRepository.findAll().collectList().block().size();

        // Update the userProfile
        UserProfile updatedUserProfile = userProfileRepository.findById(userProfile.getId()).block();
        updatedUserProfile
            .name(UPDATED_NAME)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .interests(UPDATED_INTERESTS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUserProfile.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUserProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
        UserProfile testUserProfile = userProfileList.get(userProfileList.size() - 1);
        assertThat(testUserProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserProfile.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testUserProfile.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testUserProfile.getInterests()).isEqualTo(UPDATED_INTERESTS);
    }

    @Test
    void putNonExistingUserProfile() throws Exception {
        int databaseSizeBeforeUpdate = userProfileRepository.findAll().collectList().block().size();
        userProfile.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userProfile.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserProfile() throws Exception {
        int databaseSizeBeforeUpdate = userProfileRepository.findAll().collectList().block().size();
        userProfile.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserProfile() throws Exception {
        int databaseSizeBeforeUpdate = userProfileRepository.findAll().collectList().block().size();
        userProfile.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfile))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserProfileWithPatch() throws Exception {
        // Initialize the database
        userProfileRepository.save(userProfile).block();

        int databaseSizeBeforeUpdate = userProfileRepository.findAll().collectList().block().size();

        // Update the userProfile using partial update
        UserProfile partialUpdatedUserProfile = new UserProfile();
        partialUpdatedUserProfile.setId(userProfile.getId());

        partialUpdatedUserProfile.avatar(UPDATED_AVATAR).avatarContentType(UPDATED_AVATAR_CONTENT_TYPE).interests(UPDATED_INTERESTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
        UserProfile testUserProfile = userProfileList.get(userProfileList.size() - 1);
        assertThat(testUserProfile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserProfile.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testUserProfile.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testUserProfile.getInterests()).isEqualTo(UPDATED_INTERESTS);
    }

    @Test
    void fullUpdateUserProfileWithPatch() throws Exception {
        // Initialize the database
        userProfileRepository.save(userProfile).block();

        int databaseSizeBeforeUpdate = userProfileRepository.findAll().collectList().block().size();

        // Update the userProfile using partial update
        UserProfile partialUpdatedUserProfile = new UserProfile();
        partialUpdatedUserProfile.setId(userProfile.getId());

        partialUpdatedUserProfile
            .name(UPDATED_NAME)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .interests(UPDATED_INTERESTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
        UserProfile testUserProfile = userProfileList.get(userProfileList.size() - 1);
        assertThat(testUserProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserProfile.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testUserProfile.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testUserProfile.getInterests()).isEqualTo(UPDATED_INTERESTS);
    }

    @Test
    void patchNonExistingUserProfile() throws Exception {
        int databaseSizeBeforeUpdate = userProfileRepository.findAll().collectList().block().size();
        userProfile.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserProfile() throws Exception {
        int databaseSizeBeforeUpdate = userProfileRepository.findAll().collectList().block().size();
        userProfile.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserProfile() throws Exception {
        int databaseSizeBeforeUpdate = userProfileRepository.findAll().collectList().block().size();
        userProfile.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfile))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserProfile() {
        // Initialize the database
        userProfileRepository.save(userProfile).block();

        int databaseSizeBeforeDelete = userProfileRepository.findAll().collectList().block().size();

        // Delete the userProfile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<UserProfile> userProfileList = userProfileRepository.findAll().collectList().block();
        assertThat(userProfileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
