package technology.touchmars.edu.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;
import technology.touchmars.edu.domain.UserCourse;
import technology.touchmars.edu.repository.UserCourseRepository;
import technology.touchmars.edu.service.UserCourseService;
import technology.touchmars.edu.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link technology.touchmars.edu.domain.UserCourse}.
 */
@RestController
@RequestMapping("/api")
public class UserCourseResource {

    private final Logger log = LoggerFactory.getLogger(UserCourseResource.class);

    private static final String ENTITY_NAME = "userCourse";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserCourseService userCourseService;

    private final UserCourseRepository userCourseRepository;

    public UserCourseResource(UserCourseService userCourseService, UserCourseRepository userCourseRepository) {
        this.userCourseService = userCourseService;
        this.userCourseRepository = userCourseRepository;
    }

    /**
     * {@code POST  /user-courses} : Create a new userCourse.
     *
     * @param userCourse the userCourse to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userCourse, or with status {@code 400 (Bad Request)} if the userCourse has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-courses")
    public Mono<ResponseEntity<UserCourse>> createUserCourse(@Valid @RequestBody UserCourse userCourse) throws URISyntaxException {
        log.debug("REST request to save UserCourse : {}", userCourse);
        if (userCourse.getId() != null) {
            throw new BadRequestAlertException("A new userCourse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return userCourseService
            .save(userCourse)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/user-courses/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /user-courses/:id} : Updates an existing userCourse.
     *
     * @param id the id of the userCourse to save.
     * @param userCourse the userCourse to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userCourse,
     * or with status {@code 400 (Bad Request)} if the userCourse is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userCourse couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-courses/{id}")
    public Mono<ResponseEntity<UserCourse>> updateUserCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserCourse userCourse
    ) throws URISyntaxException {
        log.debug("REST request to update UserCourse : {}, {}", id, userCourse);
        if (userCourse.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userCourse.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userCourseRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return userCourseService
                    .save(userCourse)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /user-courses/:id} : Partial updates given fields of an existing userCourse, field will ignore if it is null
     *
     * @param id the id of the userCourse to save.
     * @param userCourse the userCourse to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userCourse,
     * or with status {@code 400 (Bad Request)} if the userCourse is not valid,
     * or with status {@code 404 (Not Found)} if the userCourse is not found,
     * or with status {@code 500 (Internal Server Error)} if the userCourse couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-courses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UserCourse>> partialUpdateUserCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserCourse userCourse
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserCourse partially : {}, {}", id, userCourse);
        if (userCourse.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userCourse.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userCourseRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UserCourse> result = userCourseService.partialUpdate(userCourse);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /user-courses} : get all the userCourses.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userCourses in body.
     */
    @GetMapping("/user-courses")
    public Mono<ResponseEntity<List<UserCourse>>> getAllUserCourses(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of UserCourses");
        return userCourseService
            .countAll()
            .zipWith(userCourseService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /user-courses/:id} : get the "id" userCourse.
     *
     * @param id the id of the userCourse to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userCourse, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-courses/{id}")
    public Mono<ResponseEntity<UserCourse>> getUserCourse(@PathVariable Long id) {
        log.debug("REST request to get UserCourse : {}", id);
        Mono<UserCourse> userCourse = userCourseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userCourse);
    }

    /**
     * {@code DELETE  /user-courses/:id} : delete the "id" userCourse.
     *
     * @param id the id of the userCourse to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-courses/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteUserCourse(@PathVariable Long id) {
        log.debug("REST request to delete UserCourse : {}", id);
        return userCourseService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
