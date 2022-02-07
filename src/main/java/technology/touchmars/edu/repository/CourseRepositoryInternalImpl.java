package technology.touchmars.edu.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.Course;
import technology.touchmars.edu.domain.enumeration.CourseType;
import technology.touchmars.edu.repository.rowmapper.CourseRowMapper;

/**
 * Spring Data SQL reactive custom repository implementation for the Course entity.
 */
@SuppressWarnings("unused")
class CourseRepositoryInternalImpl extends SimpleR2dbcRepository<Course, Long> implements CourseRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CourseRowMapper courseMapper;

    private static final Table entityTable = Table.aliased("course", EntityManager.ENTITY_ALIAS);

    public CourseRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CourseRowMapper courseMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Course.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.courseMapper = courseMapper;
    }

    @Override
    public Flux<Course> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Course> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Course> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CourseSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, Course.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Course> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Course> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Course process(Row row, RowMetadata metadata) {
        Course entity = courseMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Course> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
