package technology.touchmars.edu.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import technology.touchmars.edu.domain.Interest;
import technology.touchmars.edu.repository.InterestRepository;
import technology.touchmars.edu.service.InterestService;
import technology.touchmars.edu.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link technology.touchmars.edu.domain.Interest}.
 */
@RestController
@RequestMapping("/api")
public class InterestResource {

    private final Logger log = LoggerFactory.getLogger(InterestResource.class);

    private static final String ENTITY_NAME = "interest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InterestService interestService;

    private final InterestRepository interestRepository;

    public InterestResource(InterestService interestService, InterestRepository interestRepository) {
        this.interestService = interestService;
        this.interestRepository = interestRepository;
    }

    /**
     * {@code POST  /interests} : Create a new interest.
     *
     * @param interest the interest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new interest, or with status {@code 400 (Bad Request)} if the interest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/interests")
    public Mono<ResponseEntity<Interest>> createInterest(@RequestBody Interest interest) throws URISyntaxException {
        log.debug("REST request to save Interest : {}", interest);
        if (interest.getId() != null) {
            throw new BadRequestAlertException("A new interest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return interestService
            .save(interest)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/interests/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /interests/:id} : Updates an existing interest.
     *
     * @param id the id of the interest to save.
     * @param interest the interest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interest,
     * or with status {@code 400 (Bad Request)} if the interest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the interest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/interests/{id}")
    public Mono<ResponseEntity<Interest>> updateInterest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Interest interest
    ) throws URISyntaxException {
        log.debug("REST request to update Interest : {}, {}", id, interest);
        if (interest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interestRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return interestService
                    .save(interest)
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
     * {@code PATCH  /interests/:id} : Partial updates given fields of an existing interest, field will ignore if it is null
     *
     * @param id the id of the interest to save.
     * @param interest the interest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interest,
     * or with status {@code 400 (Bad Request)} if the interest is not valid,
     * or with status {@code 404 (Not Found)} if the interest is not found,
     * or with status {@code 500 (Internal Server Error)} if the interest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/interests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Interest>> partialUpdateInterest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Interest interest
    ) throws URISyntaxException {
        log.debug("REST request to partial update Interest partially : {}, {}", id, interest);
        if (interest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interestRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Interest> result = interestService.partialUpdate(interest);

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
     * {@code GET  /interests} : get all the interests.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of interests in body.
     */
    @GetMapping("/interests")
    public Mono<ResponseEntity<List<Interest>>> getAllInterests(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Interests");
        return interestService
            .countAll()
            .zipWith(interestService.findAll(pageable).collectList())
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
     * {@code GET  /interests/:id} : get the "id" interest.
     *
     * @param id the id of the interest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the interest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/interests/{id}")
    public Mono<ResponseEntity<Interest>> getInterest(@PathVariable Long id) {
        log.debug("REST request to get Interest : {}", id);
        Mono<Interest> interest = interestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(interest);
    }

    /**
     * {@code DELETE  /interests/:id} : delete the "id" interest.
     *
     * @param id the id of the interest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/interests/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteInterest(@PathVariable Long id) {
        log.debug("REST request to delete Interest : {}", id);
        return interestService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
