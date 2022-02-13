package technology.touchmars.edu.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class UserProfileSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("avatar", table, columnPrefix + "_avatar"));
        columns.add(Column.aliased("avatar_content_type", table, columnPrefix + "_avatar_content_type"));
        columns.add(Column.aliased("interests", table, columnPrefix + "_interests"));
        columns.add(Column.aliased("profession", table, columnPrefix + "_profession"));
        columns.add(Column.aliased("industry", table, columnPrefix + "_industry"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
