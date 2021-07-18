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
import technology.touchmars.edu.domain.UserCourse;
import technology.touchmars.edu.domain.enumeration.CourseType;
import technology.touchmars.edu.repository.UserCourseRepository;
import technology.touchmars.edu.service.criteria.UserCourseCriteria;

/**
 * Integration tests for the {@link UserCourseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private static final ZonedDateTime SMALLER_BEGIN_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_DUE_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DUE_DT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

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
    private MockMvc restUserCourseMockMvc;

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

    @BeforeEach
    public void initTest() {
        userCourse = createEntity(em);
    }

    @Test
    @Transactional
    void createUserCourse() throws Exception {
        int databaseSizeBeforeCreate = userCourseRepository.findAll().size();
        // Create the UserCourse
        restUserCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userCourse)))
            .andExpect(status().isCreated());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
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
    @Transactional
    void createUserCourseWithExistingId() throws Exception {
        // Create the UserCourse with an existing ID
        userCourse.setId(1L);

        int databaseSizeBeforeCreate = userCourseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userCourse)))
            .andExpect(status().isBadRequest());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
        assertThat(userCourseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserCourses() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList
        restUserCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userCourse.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].courseType").value(hasItem(DEFAULT_COURSE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER))))
            .andExpect(jsonPath("$.[*].beginDt").value(hasItem(sameInstant(DEFAULT_BEGIN_DT))))
            .andExpect(jsonPath("$.[*].dueDt").value(hasItem(sameInstant(DEFAULT_DUE_DT))))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)));
    }

    @Test
    @Transactional
    void getUserCourse() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get the userCourse
        restUserCourseMockMvc
            .perform(get(ENTITY_API_URL_ID, userCourse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userCourse.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.courseType").value(DEFAULT_COURSE_TYPE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.coverContentType").value(DEFAULT_COVER_CONTENT_TYPE))
            .andExpect(jsonPath("$.cover").value(Base64Utils.encodeToString(DEFAULT_COVER)))
            .andExpect(jsonPath("$.beginDt").value(sameInstant(DEFAULT_BEGIN_DT)))
            .andExpect(jsonPath("$.dueDt").value(sameInstant(DEFAULT_DUE_DT)))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID));
    }

    @Test
    @Transactional
    void getUserCoursesByIdFiltering() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        Long id = userCourse.getId();

        defaultUserCourseShouldBeFound("id.equals=" + id);
        defaultUserCourseShouldNotBeFound("id.notEquals=" + id);

        defaultUserCourseShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserCourseShouldNotBeFound("id.greaterThan=" + id);

        defaultUserCourseShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserCourseShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserCoursesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where code equals to DEFAULT_CODE
        defaultUserCourseShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the userCourseList where code equals to UPDATED_CODE
        defaultUserCourseShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllUserCoursesByCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where code not equals to DEFAULT_CODE
        defaultUserCourseShouldNotBeFound("code.notEquals=" + DEFAULT_CODE);

        // Get all the userCourseList where code not equals to UPDATED_CODE
        defaultUserCourseShouldBeFound("code.notEquals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllUserCoursesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where code in DEFAULT_CODE or UPDATED_CODE
        defaultUserCourseShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the userCourseList where code equals to UPDATED_CODE
        defaultUserCourseShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllUserCoursesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where code is not null
        defaultUserCourseShouldBeFound("code.specified=true");

        // Get all the userCourseList where code is null
        defaultUserCourseShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllUserCoursesByCodeContainsSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where code contains DEFAULT_CODE
        defaultUserCourseShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the userCourseList where code contains UPDATED_CODE
        defaultUserCourseShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllUserCoursesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where code does not contain DEFAULT_CODE
        defaultUserCourseShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the userCourseList where code does not contain UPDATED_CODE
        defaultUserCourseShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllUserCoursesByCourseTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where courseType equals to DEFAULT_COURSE_TYPE
        defaultUserCourseShouldBeFound("courseType.equals=" + DEFAULT_COURSE_TYPE);

        // Get all the userCourseList where courseType equals to UPDATED_COURSE_TYPE
        defaultUserCourseShouldNotBeFound("courseType.equals=" + UPDATED_COURSE_TYPE);
    }

    @Test
    @Transactional
    void getAllUserCoursesByCourseTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where courseType not equals to DEFAULT_COURSE_TYPE
        defaultUserCourseShouldNotBeFound("courseType.notEquals=" + DEFAULT_COURSE_TYPE);

        // Get all the userCourseList where courseType not equals to UPDATED_COURSE_TYPE
        defaultUserCourseShouldBeFound("courseType.notEquals=" + UPDATED_COURSE_TYPE);
    }

    @Test
    @Transactional
    void getAllUserCoursesByCourseTypeIsInShouldWork() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where courseType in DEFAULT_COURSE_TYPE or UPDATED_COURSE_TYPE
        defaultUserCourseShouldBeFound("courseType.in=" + DEFAULT_COURSE_TYPE + "," + UPDATED_COURSE_TYPE);

        // Get all the userCourseList where courseType equals to UPDATED_COURSE_TYPE
        defaultUserCourseShouldNotBeFound("courseType.in=" + UPDATED_COURSE_TYPE);
    }

    @Test
    @Transactional
    void getAllUserCoursesByCourseTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where courseType is not null
        defaultUserCourseShouldBeFound("courseType.specified=true");

        // Get all the userCourseList where courseType is null
        defaultUserCourseShouldNotBeFound("courseType.specified=false");
    }

    @Test
    @Transactional
    void getAllUserCoursesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where name equals to DEFAULT_NAME
        defaultUserCourseShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the userCourseList where name equals to UPDATED_NAME
        defaultUserCourseShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserCoursesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where name not equals to DEFAULT_NAME
        defaultUserCourseShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the userCourseList where name not equals to UPDATED_NAME
        defaultUserCourseShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserCoursesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where name in DEFAULT_NAME or UPDATED_NAME
        defaultUserCourseShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the userCourseList where name equals to UPDATED_NAME
        defaultUserCourseShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserCoursesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where name is not null
        defaultUserCourseShouldBeFound("name.specified=true");

        // Get all the userCourseList where name is null
        defaultUserCourseShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllUserCoursesByNameContainsSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where name contains DEFAULT_NAME
        defaultUserCourseShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the userCourseList where name contains UPDATED_NAME
        defaultUserCourseShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserCoursesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where name does not contain DEFAULT_NAME
        defaultUserCourseShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the userCourseList where name does not contain UPDATED_NAME
        defaultUserCourseShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where url equals to DEFAULT_URL
        defaultUserCourseShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the userCourseList where url equals to UPDATED_URL
        defaultUserCourseShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where url not equals to DEFAULT_URL
        defaultUserCourseShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the userCourseList where url not equals to UPDATED_URL
        defaultUserCourseShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where url in DEFAULT_URL or UPDATED_URL
        defaultUserCourseShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the userCourseList where url equals to UPDATED_URL
        defaultUserCourseShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where url is not null
        defaultUserCourseShouldBeFound("url.specified=true");

        // Get all the userCourseList where url is null
        defaultUserCourseShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllUserCoursesByUrlContainsSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where url contains DEFAULT_URL
        defaultUserCourseShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the userCourseList where url contains UPDATED_URL
        defaultUserCourseShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where url does not contain DEFAULT_URL
        defaultUserCourseShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the userCourseList where url does not contain UPDATED_URL
        defaultUserCourseShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllUserCoursesByBeginDtIsEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where beginDt equals to DEFAULT_BEGIN_DT
        defaultUserCourseShouldBeFound("beginDt.equals=" + DEFAULT_BEGIN_DT);

        // Get all the userCourseList where beginDt equals to UPDATED_BEGIN_DT
        defaultUserCourseShouldNotBeFound("beginDt.equals=" + UPDATED_BEGIN_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByBeginDtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where beginDt not equals to DEFAULT_BEGIN_DT
        defaultUserCourseShouldNotBeFound("beginDt.notEquals=" + DEFAULT_BEGIN_DT);

        // Get all the userCourseList where beginDt not equals to UPDATED_BEGIN_DT
        defaultUserCourseShouldBeFound("beginDt.notEquals=" + UPDATED_BEGIN_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByBeginDtIsInShouldWork() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where beginDt in DEFAULT_BEGIN_DT or UPDATED_BEGIN_DT
        defaultUserCourseShouldBeFound("beginDt.in=" + DEFAULT_BEGIN_DT + "," + UPDATED_BEGIN_DT);

        // Get all the userCourseList where beginDt equals to UPDATED_BEGIN_DT
        defaultUserCourseShouldNotBeFound("beginDt.in=" + UPDATED_BEGIN_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByBeginDtIsNullOrNotNull() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where beginDt is not null
        defaultUserCourseShouldBeFound("beginDt.specified=true");

        // Get all the userCourseList where beginDt is null
        defaultUserCourseShouldNotBeFound("beginDt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserCoursesByBeginDtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where beginDt is greater than or equal to DEFAULT_BEGIN_DT
        defaultUserCourseShouldBeFound("beginDt.greaterThanOrEqual=" + DEFAULT_BEGIN_DT);

        // Get all the userCourseList where beginDt is greater than or equal to UPDATED_BEGIN_DT
        defaultUserCourseShouldNotBeFound("beginDt.greaterThanOrEqual=" + UPDATED_BEGIN_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByBeginDtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where beginDt is less than or equal to DEFAULT_BEGIN_DT
        defaultUserCourseShouldBeFound("beginDt.lessThanOrEqual=" + DEFAULT_BEGIN_DT);

        // Get all the userCourseList where beginDt is less than or equal to SMALLER_BEGIN_DT
        defaultUserCourseShouldNotBeFound("beginDt.lessThanOrEqual=" + SMALLER_BEGIN_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByBeginDtIsLessThanSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where beginDt is less than DEFAULT_BEGIN_DT
        defaultUserCourseShouldNotBeFound("beginDt.lessThan=" + DEFAULT_BEGIN_DT);

        // Get all the userCourseList where beginDt is less than UPDATED_BEGIN_DT
        defaultUserCourseShouldBeFound("beginDt.lessThan=" + UPDATED_BEGIN_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByBeginDtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where beginDt is greater than DEFAULT_BEGIN_DT
        defaultUserCourseShouldNotBeFound("beginDt.greaterThan=" + DEFAULT_BEGIN_DT);

        // Get all the userCourseList where beginDt is greater than SMALLER_BEGIN_DT
        defaultUserCourseShouldBeFound("beginDt.greaterThan=" + SMALLER_BEGIN_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByDueDtIsEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where dueDt equals to DEFAULT_DUE_DT
        defaultUserCourseShouldBeFound("dueDt.equals=" + DEFAULT_DUE_DT);

        // Get all the userCourseList where dueDt equals to UPDATED_DUE_DT
        defaultUserCourseShouldNotBeFound("dueDt.equals=" + UPDATED_DUE_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByDueDtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where dueDt not equals to DEFAULT_DUE_DT
        defaultUserCourseShouldNotBeFound("dueDt.notEquals=" + DEFAULT_DUE_DT);

        // Get all the userCourseList where dueDt not equals to UPDATED_DUE_DT
        defaultUserCourseShouldBeFound("dueDt.notEquals=" + UPDATED_DUE_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByDueDtIsInShouldWork() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where dueDt in DEFAULT_DUE_DT or UPDATED_DUE_DT
        defaultUserCourseShouldBeFound("dueDt.in=" + DEFAULT_DUE_DT + "," + UPDATED_DUE_DT);

        // Get all the userCourseList where dueDt equals to UPDATED_DUE_DT
        defaultUserCourseShouldNotBeFound("dueDt.in=" + UPDATED_DUE_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByDueDtIsNullOrNotNull() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where dueDt is not null
        defaultUserCourseShouldBeFound("dueDt.specified=true");

        // Get all the userCourseList where dueDt is null
        defaultUserCourseShouldNotBeFound("dueDt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserCoursesByDueDtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where dueDt is greater than or equal to DEFAULT_DUE_DT
        defaultUserCourseShouldBeFound("dueDt.greaterThanOrEqual=" + DEFAULT_DUE_DT);

        // Get all the userCourseList where dueDt is greater than or equal to UPDATED_DUE_DT
        defaultUserCourseShouldNotBeFound("dueDt.greaterThanOrEqual=" + UPDATED_DUE_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByDueDtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where dueDt is less than or equal to DEFAULT_DUE_DT
        defaultUserCourseShouldBeFound("dueDt.lessThanOrEqual=" + DEFAULT_DUE_DT);

        // Get all the userCourseList where dueDt is less than or equal to SMALLER_DUE_DT
        defaultUserCourseShouldNotBeFound("dueDt.lessThanOrEqual=" + SMALLER_DUE_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByDueDtIsLessThanSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where dueDt is less than DEFAULT_DUE_DT
        defaultUserCourseShouldNotBeFound("dueDt.lessThan=" + DEFAULT_DUE_DT);

        // Get all the userCourseList where dueDt is less than UPDATED_DUE_DT
        defaultUserCourseShouldBeFound("dueDt.lessThan=" + UPDATED_DUE_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByDueDtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where dueDt is greater than DEFAULT_DUE_DT
        defaultUserCourseShouldNotBeFound("dueDt.greaterThan=" + DEFAULT_DUE_DT);

        // Get all the userCourseList where dueDt is greater than SMALLER_DUE_DT
        defaultUserCourseShouldBeFound("dueDt.greaterThan=" + SMALLER_DUE_DT);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where userId equals to DEFAULT_USER_ID
        defaultUserCourseShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the userCourseList where userId equals to UPDATED_USER_ID
        defaultUserCourseShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUserIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where userId not equals to DEFAULT_USER_ID
        defaultUserCourseShouldNotBeFound("userId.notEquals=" + DEFAULT_USER_ID);

        // Get all the userCourseList where userId not equals to UPDATED_USER_ID
        defaultUserCourseShouldBeFound("userId.notEquals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultUserCourseShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the userCourseList where userId equals to UPDATED_USER_ID
        defaultUserCourseShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where userId is not null
        defaultUserCourseShouldBeFound("userId.specified=true");

        // Get all the userCourseList where userId is null
        defaultUserCourseShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    void getAllUserCoursesByUserIdContainsSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where userId contains DEFAULT_USER_ID
        defaultUserCourseShouldBeFound("userId.contains=" + DEFAULT_USER_ID);

        // Get all the userCourseList where userId contains UPDATED_USER_ID
        defaultUserCourseShouldNotBeFound("userId.contains=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllUserCoursesByUserIdNotContainsSomething() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        // Get all the userCourseList where userId does not contain DEFAULT_USER_ID
        defaultUserCourseShouldNotBeFound("userId.doesNotContain=" + DEFAULT_USER_ID);

        // Get all the userCourseList where userId does not contain UPDATED_USER_ID
        defaultUserCourseShouldBeFound("userId.doesNotContain=" + UPDATED_USER_ID);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserCourseShouldBeFound(String filter) throws Exception {
        restUserCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userCourse.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].courseType").value(hasItem(DEFAULT_COURSE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].coverContentType").value(hasItem(DEFAULT_COVER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cover").value(hasItem(Base64Utils.encodeToString(DEFAULT_COVER))))
            .andExpect(jsonPath("$.[*].beginDt").value(hasItem(sameInstant(DEFAULT_BEGIN_DT))))
            .andExpect(jsonPath("$.[*].dueDt").value(hasItem(sameInstant(DEFAULT_DUE_DT))))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)));

        // Check, that the count call also returns 1
        restUserCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserCourseShouldNotBeFound(String filter) throws Exception {
        restUserCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserCourse() throws Exception {
        // Get the userCourse
        restUserCourseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserCourse() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        int databaseSizeBeforeUpdate = userCourseRepository.findAll().size();

        // Update the userCourse
        UserCourse updatedUserCourse = userCourseRepository.findById(userCourse.getId()).get();
        // Disconnect from session so that the updates on updatedUserCourse are not directly saved in db
        em.detach(updatedUserCourse);
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

        restUserCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserCourse.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserCourse))
            )
            .andExpect(status().isOk());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
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
    @Transactional
    void putNonExistingUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().size();
        userCourse.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userCourse.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userCourse))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().size();
        userCourse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userCourse))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().size();
        userCourse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserCourseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userCourse)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserCourseWithPatch() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        int databaseSizeBeforeUpdate = userCourseRepository.findAll().size();

        // Update the userCourse using partial update
        UserCourse partialUpdatedUserCourse = new UserCourse();
        partialUpdatedUserCourse.setId(userCourse.getId());

        partialUpdatedUserCourse
            .courseType(UPDATED_COURSE_TYPE)
            .cover(UPDATED_COVER)
            .coverContentType(UPDATED_COVER_CONTENT_TYPE)
            .beginDt(UPDATED_BEGIN_DT)
            .dueDt(UPDATED_DUE_DT);

        restUserCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserCourse))
            )
            .andExpect(status().isOk());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
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
    @Transactional
    void fullUpdateUserCourseWithPatch() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        int databaseSizeBeforeUpdate = userCourseRepository.findAll().size();

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

        restUserCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserCourse))
            )
            .andExpect(status().isOk());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
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
    @Transactional
    void patchNonExistingUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().size();
        userCourse.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userCourse))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().size();
        userCourse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userCourse))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserCourse() throws Exception {
        int databaseSizeBeforeUpdate = userCourseRepository.findAll().size();
        userCourse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserCourseMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userCourse))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserCourse in the database
        List<UserCourse> userCourseList = userCourseRepository.findAll();
        assertThat(userCourseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserCourse() throws Exception {
        // Initialize the database
        userCourseRepository.saveAndFlush(userCourse);

        int databaseSizeBeforeDelete = userCourseRepository.findAll().size();

        // Delete the userCourse
        restUserCourseMockMvc
            .perform(delete(ENTITY_API_URL_ID, userCourse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserCourse> userCourseList = userCourseRepository.findAll();
        assertThat(userCourseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
