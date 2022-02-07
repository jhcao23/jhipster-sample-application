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
import technology.touchmars.edu.IntegrationTest;
import technology.touchmars.edu.domain.Interest;
import technology.touchmars.edu.domain.UserProfile;
import technology.touchmars.edu.domain.UserProfileInterest;
import technology.touchmars.edu.repository.EntityManager;
import technology.touchmars.edu.repository.UserProfileInterestRepository;

/**
 * Integration tests for the {@link UserProfileInterestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UserProfileInterestResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-profile-interests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserProfileInterestRepository userProfileInterestRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserProfileInterest userProfileInterest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProfileInterest createEntity(EntityManager em) {
        UserProfileInterest userProfileInterest = new UserProfileInterest().code(DEFAULT_CODE);
        // Add required entity
        UserProfile userProfile;
        userProfile = em.insert(UserProfileResourceIT.createEntity(em)).block();
        userProfileInterest.setUserProfile(userProfile);
        // Add required entity
        Interest interest;
        interest = em.insert(InterestResourceIT.createEntity(em)).block();
        userProfileInterest.setInterest(interest);
        return userProfileInterest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProfileInterest createUpdatedEntity(EntityManager em) {
        UserProfileInterest userProfileInterest = new UserProfileInterest().code(UPDATED_CODE);
        // Add required entity
        UserProfile userProfile;
        userProfile = em.insert(UserProfileResourceIT.createUpdatedEntity(em)).block();
        userProfileInterest.setUserProfile(userProfile);
        // Add required entity
        Interest interest;
        interest = em.insert(InterestResourceIT.createUpdatedEntity(em)).block();
        userProfileInterest.setInterest(interest);
        return userProfileInterest;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserProfileInterest.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserProfileResourceIT.deleteEntities(em);
        InterestResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        userProfileInterest = createEntity(em);
    }

    @Test
    void createUserProfileInterest() throws Exception {
        int databaseSizeBeforeCreate = userProfileInterestRepository.findAll().collectList().block().size();
        // Create the UserProfileInterest
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfileInterest))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeCreate + 1);
        UserProfileInterest testUserProfileInterest = userProfileInterestList.get(userProfileInterestList.size() - 1);
        assertThat(testUserProfileInterest.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    void createUserProfileInterestWithExistingId() throws Exception {
        // Create the UserProfileInterest with an existing ID
        userProfileInterest.setId(1L);

        int databaseSizeBeforeCreate = userProfileInterestRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfileInterest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllUserProfileInterests() {
        // Initialize the database
        userProfileInterestRepository.save(userProfileInterest).block();

        // Get all the userProfileInterestList
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
            .value(hasItem(userProfileInterest.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    @Test
    void getUserProfileInterest() {
        // Initialize the database
        userProfileInterestRepository.save(userProfileInterest).block();

        // Get the userProfileInterest
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userProfileInterest.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userProfileInterest.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE));
    }

    @Test
    void getNonExistingUserProfileInterest() {
        // Get the userProfileInterest
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUserProfileInterest() throws Exception {
        // Initialize the database
        userProfileInterestRepository.save(userProfileInterest).block();

        int databaseSizeBeforeUpdate = userProfileInterestRepository.findAll().collectList().block().size();

        // Update the userProfileInterest
        UserProfileInterest updatedUserProfileInterest = userProfileInterestRepository.findById(userProfileInterest.getId()).block();
        updatedUserProfileInterest.code(UPDATED_CODE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUserProfileInterest.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUserProfileInterest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeUpdate);
        UserProfileInterest testUserProfileInterest = userProfileInterestList.get(userProfileInterestList.size() - 1);
        assertThat(testUserProfileInterest.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    void putNonExistingUserProfileInterest() throws Exception {
        int databaseSizeBeforeUpdate = userProfileInterestRepository.findAll().collectList().block().size();
        userProfileInterest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userProfileInterest.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfileInterest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserProfileInterest() throws Exception {
        int databaseSizeBeforeUpdate = userProfileInterestRepository.findAll().collectList().block().size();
        userProfileInterest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfileInterest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserProfileInterest() throws Exception {
        int databaseSizeBeforeUpdate = userProfileInterestRepository.findAll().collectList().block().size();
        userProfileInterest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfileInterest))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserProfileInterestWithPatch() throws Exception {
        // Initialize the database
        userProfileInterestRepository.save(userProfileInterest).block();

        int databaseSizeBeforeUpdate = userProfileInterestRepository.findAll().collectList().block().size();

        // Update the userProfileInterest using partial update
        UserProfileInterest partialUpdatedUserProfileInterest = new UserProfileInterest();
        partialUpdatedUserProfileInterest.setId(userProfileInterest.getId());

        partialUpdatedUserProfileInterest.code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserProfileInterest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserProfileInterest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeUpdate);
        UserProfileInterest testUserProfileInterest = userProfileInterestList.get(userProfileInterestList.size() - 1);
        assertThat(testUserProfileInterest.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    void fullUpdateUserProfileInterestWithPatch() throws Exception {
        // Initialize the database
        userProfileInterestRepository.save(userProfileInterest).block();

        int databaseSizeBeforeUpdate = userProfileInterestRepository.findAll().collectList().block().size();

        // Update the userProfileInterest using partial update
        UserProfileInterest partialUpdatedUserProfileInterest = new UserProfileInterest();
        partialUpdatedUserProfileInterest.setId(userProfileInterest.getId());

        partialUpdatedUserProfileInterest.code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserProfileInterest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserProfileInterest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeUpdate);
        UserProfileInterest testUserProfileInterest = userProfileInterestList.get(userProfileInterestList.size() - 1);
        assertThat(testUserProfileInterest.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    void patchNonExistingUserProfileInterest() throws Exception {
        int databaseSizeBeforeUpdate = userProfileInterestRepository.findAll().collectList().block().size();
        userProfileInterest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userProfileInterest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfileInterest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserProfileInterest() throws Exception {
        int databaseSizeBeforeUpdate = userProfileInterestRepository.findAll().collectList().block().size();
        userProfileInterest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfileInterest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserProfileInterest() throws Exception {
        int databaseSizeBeforeUpdate = userProfileInterestRepository.findAll().collectList().block().size();
        userProfileInterest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userProfileInterest))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserProfileInterest in the database
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserProfileInterest() {
        // Initialize the database
        userProfileInterestRepository.save(userProfileInterest).block();

        int databaseSizeBeforeDelete = userProfileInterestRepository.findAll().collectList().block().size();

        // Delete the userProfileInterest
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userProfileInterest.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<UserProfileInterest> userProfileInterestList = userProfileInterestRepository.findAll().collectList().block();
        assertThat(userProfileInterestList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
