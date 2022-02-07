package technology.touchmars.edu.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import technology.touchmars.edu.domain.UserProfile;

/**
 * Converter between {@link Row} to {@link UserProfile}, with proper type conversions.
 */
@Service
public class UserProfileRowMapper implements BiFunction<Row, String, UserProfile> {

    private final ColumnConverter converter;

    public UserProfileRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserProfile} stored in the database.
     */
    @Override
    public UserProfile apply(Row row, String prefix) {
        UserProfile entity = new UserProfile();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setAvatarContentType(converter.fromRow(row, prefix + "_avatar_content_type", String.class));
        entity.setAvatar(converter.fromRow(row, prefix + "_avatar", byte[].class));
        entity.setInterests(converter.fromRow(row, prefix + "_interests", String.class));
        entity.setProfession(converter.fromRow(row, prefix + "_profession", String.class));
        entity.setIndustry(converter.fromRow(row, prefix + "_industry", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
