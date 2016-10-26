package ru.protei.portal.tools.migrate.tools;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import protei.sql.Tm_SqlHelper;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dao.PortalBaseDAO;
import ru.protei.portal.tools.migrate.parts.BaseBatchProcess;
import ru.protei.portal.tools.migrate.parts.BatchInsertTask;
import ru.protei.portal.tools.migrate.parts.BatchUpdateTask;
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

        logger.debug("running query : " + sql);

        try (PreparedStatement st = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {

            st.setFetchSize(1000);

            if (args != null && args.length > 0) {
                Tm_SqlHelper.configureStatement(st, args);
            }

            rs = st.executeQuery();


            logger.debug("loop over result-set::begin");

            while (rs.next()) {
                rez.add(Tm_SqlHelper.fetchRowAsMap(rs));
            }

            logger.debug("loop over result-set::end");
            return rez;
        }
        finally {
            Tm_SqlHelper.safeCloseResultSet(rs);
        }
    }

    public static <T> void runDefaultMigration (Connection sourceConnection,
                                                String entryId,
                                                String tableName,
                                                MigrationEntryDAO migrationEntryDAO,
                                                PortalBaseDAO<T> dao,
                                                MigrateAdapter<T> adapter) throws SQLException {

        runDefaultMigration(sourceConnection, entryId, tableName, migrationEntryDAO, dao, new BaseBatchProcess<>(), adapter);

    }

    public static <T> void runDefaultMigration (Connection sourceConnection,
                                                String entryId,
                                                String tableName,
                                                MigrationEntryDAO migrationEntryDAO,
                                                PortalBaseDAO<T> dao,
                                                BatchProcess<T> batchProcess,
                                                MigrateAdapter<T> adapter) throws SQLException {

        new BatchInsertTask(migrationEntryDAO, entryId)
                .forTable(tableName, "nID", "dtLastUpdate")
                .process(sourceConnection, dao, batchProcess, adapter)
                .dumpStats();

        new BatchUpdateTask(migrationEntryDAO, entryId)
                .forTable(tableName, "nID", "dtLastUpdate")
                .process(sourceConnection, dao, batchProcess, adapter)
                .dumpStats();

    }
}
