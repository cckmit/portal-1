package ru.protei.portal.tools.migrate.tools;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import protei.sql.Tm_SqlHelper;
import ru.protei.portal.tools.migrate.struct.Mail2Login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateUtils {

    public static final String MIGRATE_ACCOUNTS_FIX_JSON = "migrate_accounts_fix.json";
    public static Long MICHAEL_Z_ID = 18L;

    public static Long DEFAULT_CREATOR_ID = MICHAEL_Z_ID;


    private static Logger logger = Logger.getLogger(MigrateUtils.class);

    private static ObjectMapper jsonMapper;

    public static Object nvl (Object...arr) {
        for (Object v : arr) {
            if (v != null)
                return v;
        }

        return null;
    }

    static {
        jsonMapper = new ObjectMapper();
        jsonMapper.setVisibilityChecker(jsonMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }


    private static Map<String,String> _mail2loginRules;

    public static Map<String,String> getMail2LoginRules () {
        if (_mail2loginRules == null) {
            _mail2loginRules = new HashMap<>();

            try {
                for (Mail2Login entry : jsonMapper.readValue(MigrateUtils.class.getResource(MIGRATE_ACCOUNTS_FIX_JSON), Mail2Login[].class)) {
                    _mail2loginRules.put(entry.mail, entry.uid);
                }
            }
            catch (Throwable e) {
                logger.error("error while read accounts map", e);
            }
        }

        return _mail2loginRules;
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
