package technology.touchmars.edu.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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
import technology.touchmars.edu.domain.UserProfileInterest;
import technology.touchmars.edu.repository.rowmapper.InterestRowMapper;
import technology.touchmars.edu.repository.rowmapper.UserProfileInterestRowMapper;
import technology.touchmars.edu.repository.rowmapper.UserProfileRowMapper;

/**
 * Spring Data SQL reactive custom repository implementation for the UserProfileInterest entity.
 */
@SuppressWarnings("unused")
class UserProfileInterestRepositoryInternalImpl
    extends SimpleR2dbcRepository<UserProfileInterest, Long>
    implements UserProfileInterestRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserProfileRowMapper userprofileMapper;
    private final InterestRowMapper interestMapper;
    private final UserProfileInterestRowMapper userprofileinterestMapper;

    private static final Table entityTable = Table.aliased("user_profile_interest", EntityManager.ENTITY_ALIAS);
    private static final Table userProfileTable = Table.aliased("user_profile", "userProfile");
    private static final Table interestTable = Table.aliased("interest", "interest");

    public UserProfileInterestRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserProfileRowMapper userprofileMapper,
        InterestRowMapper interestMapper,
        UserProfileInterestRowMapper userprofileinterestMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(UserProfileInterest.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userprofileMapper = userprofileMapper;
        this.interestMapper = interestMapper;
        this.userprofileinterestMapper = userprofileinterestMapper;
    }

    @Override
    public Flux<UserProfileInterest> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<UserProfileInterest> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<UserProfileInterest> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = UserProfileInterestSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserProfileSqlHelper.getColumns(userProfileTable, "userProfile"));
        columns.addAll(InterestSqlHelper.getColumns(interestTable, "interest"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userProfileTable)
            .on(Column.create("user_profile_id", entityTable))
            .equals(Column.create("id", userProfileTable))
            .leftOuterJoin(interestTable)
            .on(Column.create("interest_id", entityTable))
            .equals(Column.create("id", interestTable));

        String select = entityManager.createSelect(selectFrom, UserProfileInterest.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<UserProfileInterest> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<UserProfileInterest> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private UserProfileInterest process(Row row, RowMetadata metadata) {
        UserProfileInterest entity = userprofileinterestMapper.apply(row, "e");
        entity.setUserProfile(userprofileMapper.apply(row, "userProfile"));
        entity.setInterest(interestMapper.apply(row, "interest"));
        return entity;
    }

    @Override
    public <S extends UserProfileInterest> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
