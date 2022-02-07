package technology.touchmars.edu.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class UserCourseSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("course_type", table, columnPrefix + "_course_type"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("jhi_desc", table, columnPrefix + "_jhi_desc"));
        columns.add(Column.aliased("url", table, columnPrefix + "_url"));
        columns.add(Column.aliased("cover", table, columnPrefix + "_cover"));
        columns.add(Column.aliased("cover_content_type", table, columnPrefix + "_cover_content_type"));
        columns.add(Column.aliased("begin_dt", table, columnPrefix + "_begin_dt"));
        columns.add(Column.aliased("due_dt", table, columnPrefix + "_due_dt"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("course_id", table, columnPrefix + "_course_id"));
        return columns;
    }
}
