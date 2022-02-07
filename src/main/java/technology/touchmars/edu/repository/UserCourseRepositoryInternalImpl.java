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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.touchmars.edu.domain.UserCourse;
import technology.touchmars.edu.domain.enumeration.CourseType;
import technology.touchmars.edu.repository.rowmapper.CourseRowMapper;
import technology.touchmars.edu.repository.rowmapper.UserCourseRowMapper;
import technology.touchmars.edu.repository.rowmapper.UserRowMapper;

/**
 * Spring Data SQL reactive custom repository implementation for the UserCourse entity.
 */
@SuppressWarnings("unused")
class UserCourseRepositoryInternalImpl extends SimpleR2dbcRepository<UserCourse, Long> implements UserCourseRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final CourseRowMapper courseMapper;
    private final UserCourseRowMapper usercourseMapper;

    private static final Table entityTable = Table.aliased("user_course", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table courseTable = Table.aliased("course", "course");

    public UserCourseRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        CourseRowMapper courseMapper,
        UserCourseRowMapper usercourseMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(UserCourse.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
        this.usercourseMapper = usercourseMapper;
    }

    @Override
    public Flux<UserCourse> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<UserCourse> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<UserCourse> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = UserCourseSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(CourseSqlHelper.getColumns(courseTable, "course"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(courseTable)
            .on(Column.create("course_id", entityTable))
            .equals(Column.create("id", courseTable));

        String select = entityManager.createSelect(selectFrom, UserCourse.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<UserCourse> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<UserCourse> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private UserCourse process(Row row, RowMetadata metadata) {
        UserCourse entity = usercourseMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setCourse(courseMapper.apply(row, "course"));
        return entity;
    }

    @Override
    public <S extends UserCourse> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
