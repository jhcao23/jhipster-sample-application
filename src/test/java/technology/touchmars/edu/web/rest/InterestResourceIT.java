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
import technology.touchmars.edu.repository.EntityManager;
import technology.touchmars.edu.repository.InterestRepository;

/**
 * Integration tests for the {@link InterestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InterestResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/interests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Interest interest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interest createEntity(EntityManager em) {
        Interest interest = new Interest().name(DEFAULT_NAME).code(DEFAULT_CODE);
        return interest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interest createUpdatedEntity(EntityManager em) {
        Interest interest = new Interest().name(UPDATED_NAME).code(UPDATED_CODE);
        return interest;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Interest.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        interest = createEntity(em);
    }

    @Test
    void createInterest() throws Exception {
        int databaseSizeBeforeCreate = interestRepository.findAll().collectList().block().size();
        // Create the Interest
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interest))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeCreate + 1);
        Interest testInterest = interestList.get(interestList.size() - 1);
        assertThat(testInterest.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInterest.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    void createInterestWithExistingId() throws Exception {
        // Create the Interest with an existing ID
        interest.setId(1L);

        int databaseSizeBeforeCreate = interestRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllInterests() {
        // Initialize the database
        interestRepository.save(interest).block();

        // Get all the interestList
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
            .value(hasItem(interest.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    @Test
    void getInterest() {
        // Initialize the database
        interestRepository.save(interest).block();

        // Get the interest
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, interest.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(interest.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE));
    }

    @Test
    void getNonExistingInterest() {
        // Get the interest
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewInterest() throws Exception {
        // Initialize the database
        interestRepository.save(interest).block();

        int databaseSizeBeforeUpdate = interestRepository.findAll().collectList().block().size();

        // Update the interest
        Interest updatedInterest = interestRepository.findById(interest.getId()).block();
        updatedInterest.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedInterest.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedInterest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
        Interest testInterest = interestList.get(interestList.size() - 1);
        assertThat(testInterest.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInterest.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    void putNonExistingInterest() throws Exception {
        int databaseSizeBeforeUpdate = interestRepository.findAll().collectList().block().size();
        interest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, interest.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInterest() throws Exception {
        int databaseSizeBeforeUpdate = interestRepository.findAll().collectList().block().size();
        interest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInterest() throws Exception {
        int databaseSizeBeforeUpdate = interestRepository.findAll().collectList().block().size();
        interest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interest))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInterestWithPatch() throws Exception {
        // Initialize the database
        interestRepository.save(interest).block();

        int databaseSizeBeforeUpdate = interestRepository.findAll().collectList().block().size();

        // Update the interest using partial update
        Interest partialUpdatedInterest = new Interest();
        partialUpdatedInterest.setId(interest.getId());

        partialUpdatedInterest.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInterest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInterest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
        Interest testInterest = interestList.get(interestList.size() - 1);
        assertThat(testInterest.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInterest.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    void fullUpdateInterestWithPatch() throws Exception {
        // Initialize the database
        interestRepository.save(interest).block();

        int databaseSizeBeforeUpdate = interestRepository.findAll().collectList().block().size();

        // Update the interest using partial update
        Interest partialUpdatedInterest = new Interest();
        partialUpdatedInterest.setId(interest.getId());

        partialUpdatedInterest.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInterest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInterest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
        Interest testInterest = interestList.get(interestList.size() - 1);
        assertThat(testInterest.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInterest.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    void patchNonExistingInterest() throws Exception {
        int databaseSizeBeforeUpdate = interestRepository.findAll().collectList().block().size();
        interest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, interest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(interest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInterest() throws Exception {
        int databaseSizeBeforeUpdate = interestRepository.findAll().collectList().block().size();
        interest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(interest))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInterest() throws Exception {
        int databaseSizeBeforeUpdate = interestRepository.findAll().collectList().block().size();
        interest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(interest))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInterest() {
        // Initialize the database
        interestRepository.save(interest).block();

        int databaseSizeBeforeDelete = interestRepository.findAll().collectList().block().size();

        // Delete the interest
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, interest.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Interest> interestList = interestRepository.findAll().collectList().block();
        assertThat(interestList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
