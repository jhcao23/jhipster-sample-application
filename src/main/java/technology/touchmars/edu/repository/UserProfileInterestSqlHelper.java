package technology.touchmars.edu.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class UserProfileInterestSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));

        columns.add(Column.aliased("user_profile_id", table, columnPrefix + "_user_profile_id"));
        columns.add(Column.aliased("interest_id", table, columnPrefix + "_interest_id"));
        return columns;
    }
}
