package technology.touchmars.edu.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import technology.touchmars.edu.domain.UserProfileInterest;

/**
 * Converter between {@link Row} to {@link UserProfileInterest}, with proper type conversions.
 */
@Service
public class UserProfileInterestRowMapper implements BiFunction<Row, String, UserProfileInterest> {

    private final ColumnConverter converter;

    public UserProfileInterestRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserProfileInterest} stored in the database.
     */
    @Override
    public UserProfileInterest apply(Row row, String prefix) {
        UserProfileInterest entity = new UserProfileInterest();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setUserProfileId(converter.fromRow(row, prefix + "_user_profile_id", Long.class));
        entity.setInterestId(converter.fromRow(row, prefix + "_interest_id", Long.class));
        return entity;
    }
}
