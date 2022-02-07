package technology.touchmars.edu.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import technology.touchmars.edu.domain.UserCourse;
import technology.touchmars.edu.domain.enumeration.CourseType;

/**
 * Converter between {@link Row} to {@link UserCourse}, with proper type conversions.
 */
@Service
public class UserCourseRowMapper implements BiFunction<Row, String, UserCourse> {

    private final ColumnConverter converter;

    public UserCourseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserCourse} stored in the database.
     */
    @Override
    public UserCourse apply(Row row, String prefix) {
        UserCourse entity = new UserCourse();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setCourseType(converter.fromRow(row, prefix + "_course_type", CourseType.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDesc(converter.fromRow(row, prefix + "_jhi_desc", String.class));
        entity.setUrl(converter.fromRow(row, prefix + "_url", String.class));
        entity.setCoverContentType(converter.fromRow(row, prefix + "_cover_content_type", String.class));
        entity.setCover(converter.fromRow(row, prefix + "_cover", byte[].class));
        entity.setBeginDt(converter.fromRow(row, prefix + "_begin_dt", ZonedDateTime.class));
        entity.setDueDt(converter.fromRow(row, prefix + "_due_dt", ZonedDateTime.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setCourseId(converter.fromRow(row, prefix + "_course_id", Long.class));
        return entity;
    }
}
