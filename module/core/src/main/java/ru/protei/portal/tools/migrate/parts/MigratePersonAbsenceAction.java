package ru.protei.portal.tools.migrate.parts;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.tools.migrate.tools.MigrateAction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * Created by michael on 01.04.16.
 */
public class MigratePersonAbsenceAction implements MigrateAction {

    public static final String TM_PERSON_ABS_ITEM_CODE = "ABSENCE-LOG";

    @Autowired
    PersonAbsenceDAO dao;


    @Autowired
    private MigrationEntryDAO migrateDAO;

    @Override
    public int orderOfExec() {
        return 5;
    }

    @Override
    public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException {


        new BatchProcessTask<PersonAbsence>(
                "\"AbsentLog\".Tm_AbsentViewer", "nID", migrateDAO.getLastMigratedID(TM_PERSON_ABS_ITEM_CODE, 0L)
        )
                .onBatchEnd(lastIdValue -> migrateDAO.confirmMigratedID(TM_PERSON_ABS_ITEM_CODE, lastIdValue))
                .process(src, dao, row -> {
                    PersonAbsence x = new PersonAbsence();
                    x.setId(((Number) row.get("nID")).longValue());
                    x.setCreated((Date) row.get("dtCreation"));
                    x.setCreatorId(((Number) row.get("nSubmitterID")).longValue());
                    x.setPersonId(((Number) row.get("nPersonID")).longValue());
                    x.setFromTime(joinDateTime("From", row));
                    x.setTillTime(joinDateTime("To", row));
                    x.setUserComment((String)row.get("strComment"));
                    x.setReasonId(((Number) row.get("nReasonID")).intValue());
                    return x;
                })
                .dumpStats(TM_PERSON_ABS_ITEM_CODE);

    }

    private Date joinDateTime(String item, Map<String, Object> row) {
        Date date_part = (Date) row.get("d"+item+"Date");
        Date time_part = (Date) row.get("t"+item+"Time");
        if (time_part != null) {
            date_part = DateUtils.addMilliseconds(date_part, (int)time_part.getTime());
        }
        return date_part;
    }
}
