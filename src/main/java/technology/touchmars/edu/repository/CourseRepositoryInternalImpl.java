package technology.touchmars.edu.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

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
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.Course;
import technology.touchmars.edu.domain.enumeration.CourseType;
import technology.touchmars.edu.repository.rowmapper.CourseRowMapper;
import technology.touchmars.edu.service.EntityManager;

/**
 * Spring Data SQL reactive custom repository implementation for the Course entity.
 */
@SuppressWarnings("unused")
class CourseRepositoryInternalImpl implements CourseRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CourseRowMapper courseMapper;

    private static final Table entityTable = Table.aliased("course", EntityManager.ENTITY_ALIAS);

    public CourseRepositoryInternalImpl(R2dbcEntityTemplate template, EntityManager entityManager, CourseRowMapper courseMapper) {
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
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Course> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Course> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Course process(Row row, RowMetadata metadata) {
        Course entity = courseMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Course> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Course> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Course with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Course entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CourseSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("course_type", table, columnPrefix + "_course_type"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("jhi_desc", table, columnPrefix + "_jhi_desc"));
        columns.add(Column.aliased("url", table, columnPrefix + "_url"));
        columns.add(Column.aliased("cover", table, columnPrefix + "_cover"));
        columns.add(Column.aliased("cover_content_type", table, columnPrefix + "_cover_content_type"));
        columns.add(Column.aliased("version", table, columnPrefix + "_version"));
        columns.add(Column.aliased("created_dt", table, columnPrefix + "_created_dt"));
        columns.add(Column.aliased("created_by", table, columnPrefix + "_created_by"));
        columns.add(Column.aliased("start_dt", table, columnPrefix + "_start_dt"));
        columns.add(Column.aliased("end_dt", table, columnPrefix + "_end_dt"));

        return columns;
    }
}
