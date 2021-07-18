package technology.touchmars.edu.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static technology.touchmars.edu.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import technology.touchmars.edu.IntegrationTest;
import technology.touchmars.edu.domain.Course;
import technology.touchmars.edu.domain.enumeration.CourseType;
import technology.touchmars.edu.repository.CourseRepository;
import technology.touchmars.edu.service.criteria.CourseCriteria;

/**
 * Integration tests for the {@link CourseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private static final ZonedDateTime SMALLER_CREATED_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_START_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_END_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_END_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseMockMvc;

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

    @BeforeEach
    public void initTest() {
        course = createEntity(em);
    }

    @Test
    @Transactional
    void createCourse() throws Exception {
        int databaseSizeBeforeCreate = courseRepository.findAll().size();
        // Create the Course
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isCreated());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
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
    @Transactional
    void createCourseWithExistingId() throws Exception {
        // Create the Course with an existing ID
        course.setId(1L);

        int databaseSizeBeforeCreate = courseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCourses() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].courseType").value(hasItem(DEFAULT_COURSE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER))))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].createdDt").value(hasItem(sameInstant(DEFAULT_CREATED_DT))))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].startDt").value(hasItem(sameInstant(DEFAULT_START_DT))))
            .andExpect(jsonPath("$.[*].endDt").value(hasItem(sameInstant(DEFAULT_END_DT))));
    }

    @Test
    @Transactional
    void getCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get the course
        restCourseMockMvc
            .perform(get(ENTITY_API_URL_ID, course.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(course.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.courseType").value(DEFAULT_COURSE_TYPE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.coverContentType").value(DEFAULT_COVER_CONTENT_TYPE))
            .andExpect(jsonPath("$.cover").value(Base64Utils.encodeToString(DEFAULT_COVER)))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.createdDt").value(sameInstant(DEFAULT_CREATED_DT)))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.startDt").value(sameInstant(DEFAULT_START_DT)))
            .andExpect(jsonPath("$.endDt").value(sameInstant(DEFAULT_END_DT)));
    }

    @Test
    @Transactional
    void getCoursesByIdFiltering() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        Long id = course.getId();

        defaultCourseShouldBeFound("id.equals=" + id);
        defaultCourseShouldNotBeFound("id.notEquals=" + id);

        defaultCourseShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCourseShouldNotBeFound("id.greaterThan=" + id);

        defaultCourseShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCourseShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCoursesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where code equals to DEFAULT_CODE
        defaultCourseShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the courseList where code equals to UPDATED_CODE
        defaultCourseShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCoursesByCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where code not equals to DEFAULT_CODE
        defaultCourseShouldNotBeFound("code.notEquals=" + DEFAULT_CODE);

        // Get all the courseList where code not equals to UPDATED_CODE
        defaultCourseShouldBeFound("code.notEquals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCoursesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where code in DEFAULT_CODE or UPDATED_CODE
        defaultCourseShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the courseList where code equals to UPDATED_CODE
        defaultCourseShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCoursesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where code is not null
        defaultCourseShouldBeFound("code.specified=true");

        // Get all the courseList where code is null
        defaultCourseShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByCodeContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where code contains DEFAULT_CODE
        defaultCourseShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the courseList where code contains UPDATED_CODE
        defaultCourseShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCoursesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where code does not contain DEFAULT_CODE
        defaultCourseShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the courseList where code does not contain UPDATED_CODE
        defaultCourseShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseType equals to DEFAULT_COURSE_TYPE
        defaultCourseShouldBeFound("courseType.equals=" + DEFAULT_COURSE_TYPE);

        // Get all the courseList where courseType equals to UPDATED_COURSE_TYPE
        defaultCourseShouldNotBeFound("courseType.equals=" + UPDATED_COURSE_TYPE);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseType not equals to DEFAULT_COURSE_TYPE
        defaultCourseShouldNotBeFound("courseType.notEquals=" + DEFAULT_COURSE_TYPE);

        // Get all the courseList where courseType not equals to UPDATED_COURSE_TYPE
        defaultCourseShouldBeFound("courseType.notEquals=" + UPDATED_COURSE_TYPE);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseTypeIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseType in DEFAULT_COURSE_TYPE or UPDATED_COURSE_TYPE
        defaultCourseShouldBeFound("courseType.in=" + DEFAULT_COURSE_TYPE + "," + UPDATED_COURSE_TYPE);

        // Get all the courseList where courseType equals to UPDATED_COURSE_TYPE
        defaultCourseShouldNotBeFound("courseType.in=" + UPDATED_COURSE_TYPE);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseType is not null
        defaultCourseShouldBeFound("courseType.specified=true");

        // Get all the courseList where courseType is null
        defaultCourseShouldNotBeFound("courseType.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where name equals to DEFAULT_NAME
        defaultCourseShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the courseList where name equals to UPDATED_NAME
        defaultCourseShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCoursesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where name not equals to DEFAULT_NAME
        defaultCourseShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the courseList where name not equals to UPDATED_NAME
        defaultCourseShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCoursesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCourseShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the courseList where name equals to UPDATED_NAME
        defaultCourseShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCoursesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where name is not null
        defaultCourseShouldBeFound("name.specified=true");

        // Get all the courseList where name is null
        defaultCourseShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByNameContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where name contains DEFAULT_NAME
        defaultCourseShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the courseList where name contains UPDATED_NAME
        defaultCourseShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCoursesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where name does not contain DEFAULT_NAME
        defaultCourseShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the courseList where name does not contain UPDATED_NAME
        defaultCourseShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCoursesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where url equals to DEFAULT_URL
        defaultCourseShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the courseList where url equals to UPDATED_URL
        defaultCourseShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where url not equals to DEFAULT_URL
        defaultCourseShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the courseList where url not equals to UPDATED_URL
        defaultCourseShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where url in DEFAULT_URL or UPDATED_URL
        defaultCourseShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the courseList where url equals to UPDATED_URL
        defaultCourseShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where url is not null
        defaultCourseShouldBeFound("url.specified=true");

        // Get all the courseList where url is null
        defaultCourseShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByUrlContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where url contains DEFAULT_URL
        defaultCourseShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the courseList where url contains UPDATED_URL
        defaultCourseShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where url does not contain DEFAULT_URL
        defaultCourseShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the courseList where url does not contain UPDATED_URL
        defaultCourseShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where version equals to DEFAULT_VERSION
        defaultCourseShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the courseList where version equals to UPDATED_VERSION
        defaultCourseShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where version not equals to DEFAULT_VERSION
        defaultCourseShouldNotBeFound("version.notEquals=" + DEFAULT_VERSION);

        // Get all the courseList where version not equals to UPDATED_VERSION
        defaultCourseShouldBeFound("version.notEquals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultCourseShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the courseList where version equals to UPDATED_VERSION
        defaultCourseShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where version is not null
        defaultCourseShouldBeFound("version.specified=true");

        // Get all the courseList where version is null
        defaultCourseShouldNotBeFound("version.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByVersionContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where version contains DEFAULT_VERSION
        defaultCourseShouldBeFound("version.contains=" + DEFAULT_VERSION);

        // Get all the courseList where version contains UPDATED_VERSION
        defaultCourseShouldNotBeFound("version.contains=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByVersionNotContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where version does not contain DEFAULT_VERSION
        defaultCourseShouldNotBeFound("version.doesNotContain=" + DEFAULT_VERSION);

        // Get all the courseList where version does not contain UPDATED_VERSION
        defaultCourseShouldBeFound("version.doesNotContain=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedDtIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdDt equals to DEFAULT_CREATED_DT
        defaultCourseShouldBeFound("createdDt.equals=" + DEFAULT_CREATED_DT);

        // Get all the courseList where createdDt equals to UPDATED_CREATED_DT
        defaultCourseShouldNotBeFound("createdDt.equals=" + UPDATED_CREATED_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedDtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdDt not equals to DEFAULT_CREATED_DT
        defaultCourseShouldNotBeFound("createdDt.notEquals=" + DEFAULT_CREATED_DT);

        // Get all the courseList where createdDt not equals to UPDATED_CREATED_DT
        defaultCourseShouldBeFound("createdDt.notEquals=" + UPDATED_CREATED_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedDtIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdDt in DEFAULT_CREATED_DT or UPDATED_CREATED_DT
        defaultCourseShouldBeFound("createdDt.in=" + DEFAULT_CREATED_DT + "," + UPDATED_CREATED_DT);

        // Get all the courseList where createdDt equals to UPDATED_CREATED_DT
        defaultCourseShouldNotBeFound("createdDt.in=" + UPDATED_CREATED_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedDtIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdDt is not null
        defaultCourseShouldBeFound("createdDt.specified=true");

        // Get all the courseList where createdDt is null
        defaultCourseShouldNotBeFound("createdDt.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedDtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdDt is greater than or equal to DEFAULT_CREATED_DT
        defaultCourseShouldBeFound("createdDt.greaterThanOrEqual=" + DEFAULT_CREATED_DT);

        // Get all the courseList where createdDt is greater than or equal to UPDATED_CREATED_DT
        defaultCourseShouldNotBeFound("createdDt.greaterThanOrEqual=" + UPDATED_CREATED_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedDtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdDt is less than or equal to DEFAULT_CREATED_DT
        defaultCourseShouldBeFound("createdDt.lessThanOrEqual=" + DEFAULT_CREATED_DT);

        // Get all the courseList where createdDt is less than or equal to SMALLER_CREATED_DT
        defaultCourseShouldNotBeFound("createdDt.lessThanOrEqual=" + SMALLER_CREATED_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedDtIsLessThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdDt is less than DEFAULT_CREATED_DT
        defaultCourseShouldNotBeFound("createdDt.lessThan=" + DEFAULT_CREATED_DT);

        // Get all the courseList where createdDt is less than UPDATED_CREATED_DT
        defaultCourseShouldBeFound("createdDt.lessThan=" + UPDATED_CREATED_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedDtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdDt is greater than DEFAULT_CREATED_DT
        defaultCourseShouldNotBeFound("createdDt.greaterThan=" + DEFAULT_CREATED_DT);

        // Get all the courseList where createdDt is greater than SMALLER_CREATED_DT
        defaultCourseShouldBeFound("createdDt.greaterThan=" + SMALLER_CREATED_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdBy equals to DEFAULT_CREATED_BY
        defaultCourseShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the courseList where createdBy equals to UPDATED_CREATED_BY
        defaultCourseShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdBy not equals to DEFAULT_CREATED_BY
        defaultCourseShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the courseList where createdBy not equals to UPDATED_CREATED_BY
        defaultCourseShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultCourseShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the courseList where createdBy equals to UPDATED_CREATED_BY
        defaultCourseShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdBy is not null
        defaultCourseShouldBeFound("createdBy.specified=true");

        // Get all the courseList where createdBy is null
        defaultCourseShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdBy contains DEFAULT_CREATED_BY
        defaultCourseShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the courseList where createdBy contains UPDATED_CREATED_BY
        defaultCourseShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdBy does not contain DEFAULT_CREATED_BY
        defaultCourseShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the courseList where createdBy does not contain UPDATED_CREATED_BY
        defaultCourseShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDtIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDt equals to DEFAULT_START_DT
        defaultCourseShouldBeFound("startDt.equals=" + DEFAULT_START_DT);

        // Get all the courseList where startDt equals to UPDATED_START_DT
        defaultCourseShouldNotBeFound("startDt.equals=" + UPDATED_START_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDt not equals to DEFAULT_START_DT
        defaultCourseShouldNotBeFound("startDt.notEquals=" + DEFAULT_START_DT);

        // Get all the courseList where startDt not equals to UPDATED_START_DT
        defaultCourseShouldBeFound("startDt.notEquals=" + UPDATED_START_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDtIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDt in DEFAULT_START_DT or UPDATED_START_DT
        defaultCourseShouldBeFound("startDt.in=" + DEFAULT_START_DT + "," + UPDATED_START_DT);

        // Get all the courseList where startDt equals to UPDATED_START_DT
        defaultCourseShouldNotBeFound("startDt.in=" + UPDATED_START_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDtIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDt is not null
        defaultCourseShouldBeFound("startDt.specified=true");

        // Get all the courseList where startDt is null
        defaultCourseShouldNotBeFound("startDt.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByStartDtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDt is greater than or equal to DEFAULT_START_DT
        defaultCourseShouldBeFound("startDt.greaterThanOrEqual=" + DEFAULT_START_DT);

        // Get all the courseList where startDt is greater than or equal to UPDATED_START_DT
        defaultCourseShouldNotBeFound("startDt.greaterThanOrEqual=" + UPDATED_START_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDt is less than or equal to DEFAULT_START_DT
        defaultCourseShouldBeFound("startDt.lessThanOrEqual=" + DEFAULT_START_DT);

        // Get all the courseList where startDt is less than or equal to SMALLER_START_DT
        defaultCourseShouldNotBeFound("startDt.lessThanOrEqual=" + SMALLER_START_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDtIsLessThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDt is less than DEFAULT_START_DT
        defaultCourseShouldNotBeFound("startDt.lessThan=" + DEFAULT_START_DT);

        // Get all the courseList where startDt is less than UPDATED_START_DT
        defaultCourseShouldBeFound("startDt.lessThan=" + UPDATED_START_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDt is greater than DEFAULT_START_DT
        defaultCourseShouldNotBeFound("startDt.greaterThan=" + DEFAULT_START_DT);

        // Get all the courseList where startDt is greater than SMALLER_START_DT
        defaultCourseShouldBeFound("startDt.greaterThan=" + SMALLER_START_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDtIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDt equals to DEFAULT_END_DT
        defaultCourseShouldBeFound("endDt.equals=" + DEFAULT_END_DT);

        // Get all the courseList where endDt equals to UPDATED_END_DT
        defaultCourseShouldNotBeFound("endDt.equals=" + UPDATED_END_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDt not equals to DEFAULT_END_DT
        defaultCourseShouldNotBeFound("endDt.notEquals=" + DEFAULT_END_DT);

        // Get all the courseList where endDt not equals to UPDATED_END_DT
        defaultCourseShouldBeFound("endDt.notEquals=" + UPDATED_END_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDtIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDt in DEFAULT_END_DT or UPDATED_END_DT
        defaultCourseShouldBeFound("endDt.in=" + DEFAULT_END_DT + "," + UPDATED_END_DT);

        // Get all the courseList where endDt equals to UPDATED_END_DT
        defaultCourseShouldNotBeFound("endDt.in=" + UPDATED_END_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDtIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDt is not null
        defaultCourseShouldBeFound("endDt.specified=true");

        // Get all the courseList where endDt is null
        defaultCourseShouldNotBeFound("endDt.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByEndDtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDt is greater than or equal to DEFAULT_END_DT
        defaultCourseShouldBeFound("endDt.greaterThanOrEqual=" + DEFAULT_END_DT);

        // Get all the courseList where endDt is greater than or equal to UPDATED_END_DT
        defaultCourseShouldNotBeFound("endDt.greaterThanOrEqual=" + UPDATED_END_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDt is less than or equal to DEFAULT_END_DT
        defaultCourseShouldBeFound("endDt.lessThanOrEqual=" + DEFAULT_END_DT);

        // Get all the courseList where endDt is less than or equal to SMALLER_END_DT
        defaultCourseShouldNotBeFound("endDt.lessThanOrEqual=" + SMALLER_END_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDtIsLessThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDt is less than DEFAULT_END_DT
        defaultCourseShouldNotBeFound("endDt.lessThan=" + DEFAULT_END_DT);

        // Get all the courseList where endDt is less than UPDATED_END_DT
        defaultCourseShouldBeFound("endDt.lessThan=" + UPDATED_END_DT);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDt is greater than DEFAULT_END_DT
        defaultCourseShouldNotBeFound("endDt.greaterThan=" + DEFAULT_END_DT);

        // Get all the courseList where endDt is greater than SMALLER_END_DT
        defaultCourseShouldBeFound("endDt.greaterThan=" + SMALLER_END_DT);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseShouldBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].courseType").value(hasItem(DEFAULT_COURSE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER))))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].createdDt").value(hasItem(sameInstant(DEFAULT_CREATED_DT))))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].startDt").value(hasItem(sameInstant(DEFAULT_START_DT))))
            .andExpect(jsonPath("$.[*].endDt").value(hasItem(sameInstant(DEFAULT_END_DT))));

        // Check, that the count call also returns 1
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseShouldNotBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourse() throws Exception {
        // Get the course
        restCourseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

        // Update the course
        Course updatedCourse = courseRepository.findById(course.getId()).get();
        // Disconnect from session so that the updates on updatedCourse are not directly saved in db
        em.detach(updatedCourse);
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

        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCourse.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
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
    @Transactional
    void putNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, course.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(course))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(course))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

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

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
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
    @Transactional
    void fullUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

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

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
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
    @Transactional
    void patchNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, course.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(course))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(course))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(course)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeDelete = courseRepository.findAll().size();

        // Delete the course
        restCourseMockMvc
            .perform(delete(ENTITY_API_URL_ID, course.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
