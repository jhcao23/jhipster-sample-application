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
import technology.touchmars.edu.domain.Course;
import technology.touchmars.edu.domain.enumeration.CourseType;
import technology.touchmars.edu.repository.CourseRepository;
import technology.touchmars.edu.service.EntityManager;

/**
 * Integration tests for the {@link CourseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CourseResourceIT {

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

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Course course;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createEntity(EntityManager em) {
        Course course = new Course()
            .code(DEFAULT_CODE)
            .courseType(DEFAULT_COURSE_TYPE)
            .name(DEFAULT_NAME)
            .desc(DEFAULT_DESC)
            .url(DEFAULT_URL)
            .cover(DEFAULT_COVER)
            .coverContentType(DEFAULT_COVER_CONTENT_TYPE)
            .version(DEFAULT_VERSION)
            .createdDt(DEFAULT_CREATED_DT)
            .createdBy(DEFAULT_CREATED_BY)
            .startDt(DEFAULT_START_DT)
            .endDt(DEFAULT_END_DT);
        return course;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createUpdatedEntity(EntityManager em) {
        Course course = new Course()
            .code(UPDATED_CODE)
            .courseType(UPDATED_COURSE_TYPE)
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .url(UPDATED_URL)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .version(UPDATED_VERSION)
            .createdDt(UPDATED_CREATED_DT)
            .createdBy(UPDATED_CREATED_BY)
            .startDt(UPDATED_START_DT)
            .endDt(UPDATED_END_DT);
        return course;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Course.class).block();
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
        course = createEntity(em);
    }

    @Test
    void createCourse() throws Exception {
        int databaseSizeBeforeCreate = courseRepository.findAll().collectList().block().size();
        // Create the Course
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(course))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeCreate + 1);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCourse.getCourseType()).isEqualTo(DEFAULT_COURSE_TYPE);
        assertThat(testCourse.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCourse.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testCourse.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testCourse.getCover()).isEqualTo(DEFAULT_COVER);
        assertThat(testCourse.getCoverContentType()).isEqualTo(DEFAULT_COVER_CONTENT_TYPE);
        assertThat(testCourse.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testCourse.getCreatedDt()).isEqualTo(DEFAULT_CREATED_DT);
        assertThat(testCourse.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCourse.getStartDt()).isEqualTo(DEFAULT_START_DT);
        assertThat(testCourse.getEndDt()).isEqualTo(DEFAULT_END_DT);
    }

    @Test
    void createCourseWithExistingId() throws Exception {
        // Create the Course with an existing ID
        course.setId(1L);

        int databaseSizeBeforeCreate = courseRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(course))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCourses() {
        // Initialize the database
        courseRepository.save(course).block();

        // Get all the courseList
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
            .value(hasItem(course.getId().intValue()))
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
            .jsonPath("$.[*].version")
            .value(hasItem(DEFAULT_VERSION))
            .jsonPath("$.[*].createdDt")
            .value(hasItem(sameInstant(DEFAULT_CREATED_DT)))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].startDt")
            .value(hasItem(sameInstant(DEFAULT_START_DT)))
            .jsonPath("$.[*].endDt")
            .value(hasItem(sameInstant(DEFAULT_END_DT)));
    }

    @Test
    void getCourse() {
        // Initialize the database
        courseRepository.save(course).block();

        // Get the course
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, course.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(course.getId().intValue()))
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
            .jsonPath("$.version")
            .value(is(DEFAULT_VERSION))
            .jsonPath("$.createdDt")
            .value(is(sameInstant(DEFAULT_CREATED_DT)))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY))
            .jsonPath("$.startDt")
            .value(is(sameInstant(DEFAULT_START_DT)))
            .jsonPath("$.endDt")
            .value(is(sameInstant(DEFAULT_END_DT)));
    }

    @Test
    void getNonExistingCourse() {
        // Get the course
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCourse() throws Exception {
        // Initialize the database
        courseRepository.save(course).block();

        int databaseSizeBeforeUpdate = courseRepository.findAll().collectList().block().size();

        // Update the course
        Course updatedCourse = courseRepository.findById(course.getId()).block();
        updatedCourse
            .code(UPDATED_CODE)
            .courseType(UPDATED_COURSE_TYPE)
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .url(UPDATED_URL)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .version(UPDATED_VERSION)
            .createdDt(UPDATED_CREATED_DT)
            .createdBy(UPDATED_CREATED_BY)
            .startDt(UPDATED_START_DT)
            .endDt(UPDATED_END_DT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCourse.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCourse))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCourse.getCourseType()).isEqualTo(UPDATED_COURSE_TYPE);
        assertThat(testCourse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCourse.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testCourse.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testCourse.getCover()).isEqualTo(UPDATED_COVER);
        assertThat(testCourse.getCoverContentType()).isEqualTo(UPDATED_COVER_CONTENT_TYPE);
        assertThat(testCourse.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testCourse.getCreatedDt()).isEqualTo(UPDATED_CREATED_DT);
        assertThat(testCourse.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCourse.getStartDt()).isEqualTo(UPDATED_START_DT);
        assertThat(testCourse.getEndDt()).isEqualTo(UPDATED_END_DT);
    }

    @Test
    void putNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().collectList().block().size();
        course.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, course.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(course))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().collectList().block().size();
        course.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(course))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().collectList().block().size();
        course.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(course))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.save(course).block();

        int databaseSizeBeforeUpdate = courseRepository.findAll().collectList().block().size();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .name(UPDATED_NAME)
            .url(UPDATED_URL)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .createdDt(UPDATED_CREATED_DT)
            .endDt(UPDATED_END_DT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCourse.getCourseType()).isEqualTo(DEFAULT_COURSE_TYPE);
        assertThat(testCourse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCourse.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testCourse.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testCourse.getCover()).isEqualTo(UPDATED_COVER);
        assertThat(testCourse.getCoverContentType()).isEqualTo(UPDATED_COVER_CONTENT_TYPE);
        assertThat(testCourse.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testCourse.getCreatedDt()).isEqualTo(UPDATED_CREATED_DT);
        assertThat(testCourse.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCourse.getStartDt()).isEqualTo(DEFAULT_START_DT);
        assertThat(testCourse.getEndDt()).isEqualTo(UPDATED_END_DT);
    }

    @Test
    void fullUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.save(course).block();

        int databaseSizeBeforeUpdate = courseRepository.findAll().collectList().block().size();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .code(UPDATED_CODE)
            .courseType(UPDATED_COURSE_TYPE)
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .url(UPDATED_URL)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .version(UPDATED_VERSION)
            .createdDt(UPDATED_CREATED_DT)
            .createdBy(UPDATED_CREATED_BY)
            .startDt(UPDATED_START_DT)
            .endDt(UPDATED_END_DT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCourse.getCourseType()).isEqualTo(UPDATED_COURSE_TYPE);
        assertThat(testCourse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCourse.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testCourse.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testCourse.getCover()).isEqualTo(UPDATED_COVER);
        assertThat(testCourse.getCoverContentType()).isEqualTo(UPDATED_COVER_CONTENT_TYPE);
        assertThat(testCourse.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testCourse.getCreatedDt()).isEqualTo(UPDATED_CREATED_DT);
        assertThat(testCourse.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCourse.getStartDt()).isEqualTo(UPDATED_START_DT);
        assertThat(testCourse.getEndDt()).isEqualTo(UPDATED_END_DT);
    }

    @Test
    void patchNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().collectList().block().size();
        course.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, course.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(course))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().collectList().block().size();
        course.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(course))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().collectList().block().size();
        course.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(course))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCourse() {
        // Initialize the database
        courseRepository.save(course).block();

        int databaseSizeBeforeDelete = courseRepository.findAll().collectList().block().size();

        // Delete the course
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, course.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Course> courseList = courseRepository.findAll().collectList().block();
        assertThat(courseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
