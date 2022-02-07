package technology.touchmars.edu.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import technology.touchmars.edu.domain.Course;
import technology.touchmars.edu.domain.enumeration.CourseType;

/**
 * Converter between {@link Row} to {@link Course}, with proper type conversions.
 */
@Service
public class CourseRowMapper implements BiFunction<Row, String, Course> {

    private final ColumnConverter converter;

    public CourseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Course} stored in the database.
     */
    @Override
    public Course apply(Row row, String prefix) {
        Course entity = new Course();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setCourseType(converter.fromRow(row, prefix + "_course_type", CourseType.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDesc(converter.fromRow(row, prefix + "_jhi_desc", String.class));
        entity.setUrl(converter.fromRow(row, prefix + "_url", String.class));
        entity.setCoverContentType(converter.fromRow(row, prefix + "_cover_content_type", String.class));
        entity.setCover(converter.fromRow(row, prefix + "_cover", byte[].class));
        entity.setVersion(converter.fromRow(row, prefix + "_version", String.class));
        entity.setCreatedDt(converter.fromRow(row, prefix + "_created_dt", ZonedDateTime.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setStartDt(converter.fromRow(row, prefix + "_start_dt", ZonedDateTime.class));
        entity.setEndDt(converter.fromRow(row, prefix + "_end_dt", ZonedDateTime.class));
        return entity;
    }
}
