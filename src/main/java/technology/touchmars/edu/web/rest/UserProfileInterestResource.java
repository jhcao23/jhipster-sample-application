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
import technology.touchmars.edu.domain.UserProfileInterest;
import technology.touchmars.edu.repository.UserProfileInterestRepository;
import technology.touchmars.edu.service.UserProfileInterestService;
import technology.touchmars.edu.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link technology.touchmars.edu.domain.UserProfileInterest}.
 */
@RestController
@RequestMapping("/api")
public class UserProfileInterestResource {

    private final Logger log = LoggerFactory.getLogger(UserProfileInterestResource.class);

    private static final String ENTITY_NAME = "userProfileInterest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserProfileInterestService userProfileInterestService;

    private final UserProfileInterestRepository userProfileInterestRepository;

    public UserProfileInterestResource(
        UserProfileInterestService userProfileInterestService,
        UserProfileInterestRepository userProfileInterestRepository
    ) {
        this.userProfileInterestService = userProfileInterestService;
        this.userProfileInterestRepository = userProfileInterestRepository;
    }

    /**
     * {@code POST  /user-profile-interests} : Create a new userProfileInterest.
     *
     * @param userProfileInterest the userProfileInterest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userProfileInterest, or with status {@code 400 (Bad Request)} if the userProfileInterest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-profile-interests")
    public Mono<ResponseEntity<UserProfileInterest>> createUserProfileInterest(@Valid @RequestBody UserProfileInterest userProfileInterest)
        throws URISyntaxException {
        log.debug("REST request to save UserProfileInterest : {}", userProfileInterest);
        if (userProfileInterest.getId() != null) {
            throw new BadRequestAlertException("A new userProfileInterest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return userProfileInterestService
            .save(userProfileInterest)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/user-profile-interests/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /user-profile-interests/:id} : Updates an existing userProfileInterest.
     *
     * @param id the id of the userProfileInterest to save.
     * @param userProfileInterest the userProfileInterest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userProfileInterest,
     * or with status {@code 400 (Bad Request)} if the userProfileInterest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userProfileInterest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-profile-interests/{id}")
    public Mono<ResponseEntity<UserProfileInterest>> updateUserProfileInterest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserProfileInterest userProfileInterest
    ) throws URISyntaxException {
        log.debug("REST request to update UserProfileInterest : {}, {}", id, userProfileInterest);
        if (userProfileInterest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userProfileInterest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userProfileInterestRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return userProfileInterestService
                    .save(userProfileInterest)
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
     * {@code PATCH  /user-profile-interests/:id} : Partial updates given fields of an existing userProfileInterest, field will ignore if it is null
     *
     * @param id the id of the userProfileInterest to save.
     * @param userProfileInterest the userProfileInterest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userProfileInterest,
     * or with status {@code 400 (Bad Request)} if the userProfileInterest is not valid,
     * or with status {@code 404 (Not Found)} if the userProfileInterest is not found,
     * or with status {@code 500 (Internal Server Error)} if the userProfileInterest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-profile-interests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UserProfileInterest>> partialUpdateUserProfileInterest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserProfileInterest userProfileInterest
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserProfileInterest partially : {}, {}", id, userProfileInterest);
        if (userProfileInterest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userProfileInterest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userProfileInterestRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UserProfileInterest> result = userProfileInterestService.partialUpdate(userProfileInterest);

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
     * {@code GET  /user-profile-interests} : get all the userProfileInterests.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userProfileInterests in body.
     */
    @GetMapping("/user-profile-interests")
    public Mono<ResponseEntity<List<UserProfileInterest>>> getAllUserProfileInterests(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of UserProfileInterests");
        return userProfileInterestService
            .countAll()
            .zipWith(userProfileInterestService.findAll(pageable).collectList())
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
     * {@code GET  /user-profile-interests/:id} : get the "id" userProfileInterest.
     *
     * @param id the id of the userProfileInterest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userProfileInterest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-profile-interests/{id}")
    public Mono<ResponseEntity<UserProfileInterest>> getUserProfileInterest(@PathVariable Long id) {
        log.debug("REST request to get UserProfileInterest : {}", id);
        Mono<UserProfileInterest> userProfileInterest = userProfileInterestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userProfileInterest);
    }

    /**
     * {@code DELETE  /user-profile-interests/:id} : delete the "id" userProfileInterest.
     *
     * @param id the id of the userProfileInterest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-profile-interests/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteUserProfileInterest(@PathVariable Long id) {
        log.debug("REST request to delete UserProfileInterest : {}", id);
        return userProfileInterestService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
