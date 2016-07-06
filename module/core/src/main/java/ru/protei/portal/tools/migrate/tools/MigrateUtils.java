package ru.protei.portal.tools.migrate.tools;

import protei.sql.Tm_SqlHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateUtils {

    public static Long MICHAEL_Z_ID = 18L;

    public static Long DEFAULT_CREATOR_ID = MICHAEL_Z_ID;

    public static Object nvl (Object...arr) {
        for (Object v : arr) {
            if (v != null)
                return v;
        }

        return null;
    }


    public static List<Map<String,Object>> buildListForTable (Connection conn, String tableName, String orderBy) throws SQLException {
        String sql = "select * from " + tableName + " order by " + orderBy;
        return mapSqlQuery(conn, sql, (Object[]) null);
    }


    public static List<Map<String, Object>> mapSqlQuery(Connection conn, String sql, Object... args) throws SQLException {
        List<Map<String,Object>> rez = new ArrayList<Map<String,Object>>();
        ResultSet rs = null;

        System.out.println("running query : " + sql);

        try (PreparedStatement st = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {

            st.setFetchSize(1000);

            if (args != null && args.length > 0) {
                Tm_SqlHelper.configureStatement(st, args);
            }

            rs = st.executeQuery();


            System.out.println("loop over result-set::begin");

            while (rs.next()) {
                rez.add(Tm_SqlHelper.fetchRowAsMap(rs));
            }

            System.out.println("loop over result-set::end");
            return rez;
        }
        finally {
            Tm_SqlHelper.safeCloseResultSet(rs);
        }
    }
}
