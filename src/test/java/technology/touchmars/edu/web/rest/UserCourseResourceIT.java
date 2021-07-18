package technology.touchmars.edu.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static technology.touchmars.edu.web.rest.TestUtil.sameInstant;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import technology.touchmars.edu.domain.UserCourse;
import technology.touchmars.edu.domain.enumeration.CourseType;
import technology.touchmars.edu.repository.UserCourseRepository;
import technology.touchmars.edu.service.EntityManager;

/**
 * Integration tests for the {@link UserCourseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class UserCourseResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final CourseType DEFAULT_COURSE_TYPE = CourseType.PROGRAM;
    private static final CourseType UPDATED_COURSE_TYPE = CourseType.SPECIALIZATION;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final byte[] DEFAULT_COVER = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_COVER = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_COVER_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_COVER_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_BEGIN_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_BEGIN_DT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DUE_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserCourse userCourse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserCourse createEntity(EntityManager em) {
        UserCourse userCourse = new UserCourse()
            .code(DEFAULT_CODE)
            .courseType(DEFAULT_COURSE_TYPE)
            .name(DEFAULT_NAME)
            .desc(DEFAULT_DESC)
            .url(DEFAULT_URL)
            .cover(DEFAULT_COVER)
            .coverContentType(DEFAULT_COVER_CONTENT_TYPE)
            .beginDt(DEFAULT_BEGIN_DT)
            .dueDt(DEFAULT_DUE_DT)
            .userId(DEFAULT_USER_ID);
        return userCourse;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserCourse createUpdatedEntity(EntityManager em) {
        UserCourse userCourse = new UserCourse()
            .code(UPDATED_CODE)
            .courseType(UPDATED_COURSE_TYPE)
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .url(UPDATED_URL)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .beginDt(UPDATED_BEGIN_DT)
            .dueDt(UPDATED_DUE_DT)
            .userId(UPDATED_USER_ID);
        return userCourse;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserCourse.class).block();
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
        userCourse = createEntity(em);
    }

    @Test
    void createUserCourse() throws Exception {
        int databaseSizeBeforeCreate = userCourseRepository.findAll().collectList().block().size();
        // Create the UserCourse
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userCourse))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeCreate + 1);
        UserCourse testUserCourse = userCourseList.get(userCourseList.size() - 1);
        assertThat(testUserCourse.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testUserCourse.getCourseType()).isEqualTo(DEFAULT_COURSE_TYPE);
        assertThat(testUserCourse.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserCourse.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testUserCourse.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testUserCourse.getCover()).isEqualTo(DEFAULT_COVER);
        assertThat(testUserCourse.getCoverContentType()).isEqualTo(DEFAULT_COVER_CONTENT_TYPE);
        assertThat(testUserCourse.getBeginDt()).isEqualTo(DEFAULT_BEGIN_DT);
        assertThat(testUserCourse.getDueDt()).isEqualTo(DEFAULT_DUE_DT);
        assertThat(testUserCourse.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    void createUserCourseWithExistingId() throws Exception {
        // Create the UserCourse with an existing ID
        userCourse.setId(1L);

        int databaseSizeBeforeCreate = userCourseRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userCourse))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllUserCourses() {
        // Initialize the database
        userCourseRepository.save(userCourse).block();

        // Get all the userCourseList
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
            .value(hasItem(userCourse.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].courseType")
            .value(hasItem(DEFAULT_COURSE_TYPE.toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].desc")
            .value(hasItem(DEFAULT_DESC.toString()))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL))
            .jsonPath("$.[*].coverContentType")
            .value(hasItem(DEFAULT_COVER_CONTENT_TYPE))
            .jsonPath("$.[*].cover")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER)))
            .jsonPath("$.[*].beginDt")
            .value(hasItem(sameInstant(DEFAULT_BEGIN_DT)))
            .jsonPath("$.[*].dueDt")
            .value(hasItem(sameInstant(DEFAULT_DUE_DT)))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID));
    }

    @Test
    void getUserCourse() {
        // Initialize the database
        userCourseRepository.save(userCourse).block();

        // Get the userCourse
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userCourse.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userCourse.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.courseType")
            .value(is(DEFAULT_COURSE_TYPE.toString()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.desc")
            .value(is(DEFAULT_DESC.toString()))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL))
            .jsonPath("$.coverContentType")
            .value(is(DEFAULT_COVER_CONTENT_TYPE))
            .jsonPath("$.cover")
            .value(is(Base64Utils.encodeToString(DEFAULT_COVER)))
            .jsonPath("$.beginDt")
            .value(is(sameInstant(DEFAULT_BEGIN_DT)))
            .jsonPath("$.dueDt")
            .value(is(sameInstant(DEFAULT_DUE_DT)))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID));
    }

    @Test
    void getNonExistingUserCourse() {
        // Get the userCourse
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUserCourse() throws Exception {
        // Initialize the database
        userCourseRepository.save(userCourse).block();

        int databaseSizeBeforeUpdate = userCourseRepository.findAll().collectList().block().size();

        // Update the userCourse
        UserCourse updatedUserCourse = userCourseRepository.findById(userCourse.getId()).block();
        updatedUserCourse
            .code(UPDATED_CODE)
            .courseType(UPDATED_COURSE_TYPE)
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .url(UPDATED_URL)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .beginDt(UPDATED_BEGIN_DT)
            .dueDt(UPDATED_DUE_DT)
            .userId(UPDATED_USER_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUserCourse.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUserCourse))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
        UserCourse testUserCourse = userCourseList.get(userCourseList.size() - 1);
        assertThat(testUserCourse.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testUserCourse.getCourseType()).isEqualTo(UPDATED_COURSE_TYPE);
        assertThat(testUserCourse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserCourse.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testUserCourse.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testUserCourse.getCover()).isEqualTo(UPDATED_COVER);
        assertThat(testUserCourse.getCoverContentType()).isEqualTo(UPDATED_COVER_CONTENT_TYPE);
        assertThat(testUserCourse.getBeginDt()).isEqualTo(UPDATED_BEGIN_DT);
        assertThat(testUserCourse.getDueDt()).isEqualTo(UPDATED_DUE_DT);
        assertThat(testUserCourse.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void putNonExistingUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().collectList().block().size();
        userCourse.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userCourse.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userCourse))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().collectList().block().size();
        userCourse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userCourse))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().collectList().block().size();
        userCourse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userCourse))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserCourseWithPatch() throws Exception {
        // Initialize the database
        userCourseRepository.save(userCourse).block();

        int databaseSizeBeforeUpdate = userCourseRepository.findAll().collectList().block().size();

        // Update the userCourse using partial update
        UserCourse partialUpdatedUserCourse = new UserCourse();
        partialUpdatedUserCourse.setId(userCourse.getId());

        partialUpdatedUserCourse
            .courseType(UPDATED_COURSE_TYPE)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .beginDt(UPDATED_BEGIN_DT)
            .dueDt(UPDATED_DUE_DT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserCourse.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserCourse))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
        UserCourse testUserCourse = userCourseList.get(userCourseList.size() - 1);
        assertThat(testUserCourse.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testUserCourse.getCourseType()).isEqualTo(UPDATED_COURSE_TYPE);
        assertThat(testUserCourse.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserCourse.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testUserCourse.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testUserCourse.getCover()).isEqualTo(UPDATED_COVER);
        assertThat(testUserCourse.getCoverContentType()).isEqualTo(UPDATED_COVER_CONTENT_TYPE);
        assertThat(testUserCourse.getBeginDt()).isEqualTo(UPDATED_BEGIN_DT);
        assertThat(testUserCourse.getDueDt()).isEqualTo(UPDATED_DUE_DT);
        assertThat(testUserCourse.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    void fullUpdateUserCourseWithPatch() throws Exception {
        // Initialize the database
        userCourseRepository.save(userCourse).block();

        int databaseSizeBeforeUpdate = userCourseRepository.findAll().collectList().block().size();

        // Update the userCourse using partial update
        UserCourse partialUpdatedUserCourse = new UserCourse();
        partialUpdatedUserCourse.setId(userCourse.getId());

        partialUpdatedUserCourse
            .code(UPDATED_CODE)
            .courseType(UPDATED_COURSE_TYPE)
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .url(UPDATED_URL)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .beginDt(UPDATED_BEGIN_DT)
            .dueDt(UPDATED_DUE_DT)
            .userId(UPDATED_USER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserCourse.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserCourse))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
        UserCourse testUserCourse = userCourseList.get(userCourseList.size() - 1);
        assertThat(testUserCourse.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testUserCourse.getCourseType()).isEqualTo(UPDATED_COURSE_TYPE);
        assertThat(testUserCourse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserCourse.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testUserCourse.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testUserCourse.getCover()).isEqualTo(UPDATED_COVER);
        assertThat(testUserCourse.getCoverContentType()).isEqualTo(UPDATED_COVER_CONTENT_TYPE);
        assertThat(testUserCourse.getBeginDt()).isEqualTo(UPDATED_BEGIN_DT);
        assertThat(testUserCourse.getDueDt()).isEqualTo(UPDATED_DUE_DT);
        assertThat(testUserCourse.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void patchNonExistingUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().collectList().block().size();
        userCourse.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userCourse.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userCourse))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().collectList().block().size();
        userCourse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userCourse))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().collectList().block().size();
        userCourse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userCourse))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserCourse() {
        // Initialize the database
        userCourseRepository.save(userCourse).block();

        int databaseSizeBeforeDelete = userCourseRepository.findAll().collectList().block().size();

        // Delete the userCourse
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userCourse.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<UserCourse> userCourseList = userCourseRepository.findAll().collectList().block();
        assertThat(userCourseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
