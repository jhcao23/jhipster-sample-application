package technology.touchmars.edu.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import technology.touchmars.edu.domain.Interest;

/**
 * Converter between {@link Row} to {@link Interest}, with proper type conversions.
 */
@Service
public class InterestRowMapper implements BiFunction<Row, String, Interest> {

    private final ColumnConverter converter;

    public InterestRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Interest} stored in the database.
     */
    @Override
    public Interest apply(Row row, String prefix) {
        Interest entity = new Interest();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        return entity;
    }
}
