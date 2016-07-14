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
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by michael on 01.04.16.
 */
public class MigratePersonAbsenceAction implements MigrateAction {

    public static final String TM_PERSON_ABS_ITEM_CODE = "ABSENCE-LOG";
    private Calendar calendar = Calendar.getInstance();

    @Autowired
    PersonAbsenceDAO dao;

    @Autowired
    private MigrationEntryDAO migrateDAO;

    @Override
    public int orderOfExec() {
        return 5;
    }

//    public static Logger log;
//    static { // ДЛЯ ПРОВЕРКИ
//        try {
//            System.setProperty("java.util.logging.config.file", "/home/bondarenko/portal/module/web-ui/src/main/java/ru/protei/portal/webui/controller/dict/logging.properties");
//            LogManager.getLogManager().readConfiguration();
//        }catch (Throwable e){
//            System.out.println(e);
//        }
//        log = Logger.getLogger("PORTAL");
//    }


    @Override
    public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException {

        long lastOldDateUpdate = migrateDAO.getMigratedLastUpdate(TM_PERSON_ABS_ITEM_CODE, 0L);
        migrateDAO.confirmMigratedLastUpdate(TM_PERSON_ABS_ITEM_CODE, new Date().getTime());


        new BatchProcessTask<PersonAbsence>(
                "\"AbsentLog\".Tm_AbsentViewer", "dtLastUpdate", lastOldDateUpdate
        )
                .withIdFieldName("nID")
                .setLastId(migrateDAO.getMigratedLastId(TM_PERSON_ABS_ITEM_CODE, 0L))
                .setLastUpdate(lastOldDateUpdate)

                .onBatchEnd(lastIdValue -> migrateDAO.confirmMigratedLastId(TM_PERSON_ABS_ITEM_CODE, lastIdValue))
                .process(src, dao, row -> {
                    PersonAbsence x = new PersonAbsence();
                    x.setId(((Number) row.get("nID")).longValue());
                    x.setCreated((Date) row.get("dtCreation"));
                    x.setCreatorId(((Number) row.get("nSubmitterID")).longValue());
                    x.setPersonId(((Number) row.get("nPersonID")).longValue());
                    x.setUserComment((String) row.get("strComment"));
                    x.setReasonId(((Number) row.get("nReasonID")).intValue());


                    Date dateFrom = joinDateTime("From", row);
                    Date dateTill = joinDateTime("To", row);
                    if (dateFrom.getTime() > dateTill.getTime()) {
                        // swap dates
                        Date temp = dateFrom;
                        dateFrom = dateTill;
                        dateTill = temp;
                    }

                    x.setFromTime(dateFrom);
                    x.setTillTime(dateTill);

                    return x;
                })
                .dumpStats(TM_PERSON_ABS_ITEM_CODE);

    }

    private String getHMS(Date date){
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
    }

    private Date joinDateTime(String item, Map<String, Object> row) {
        Date date_part = (Date) row.get("d"+item+"Date");
        Date time_part = (Date) row.get("t"+item+"Time");
        if (time_part != null) {
            //date_part = DateUtils.addMilliseconds(date_part, (int)time_part.getTime()); не работает, т.к time_part может иметь год, месяц и число, отличные от date_part
            calendar.setTime(time_part);
            date_part = DateUtils.setHours(date_part, calendar.get(Calendar.HOUR_OF_DAY));
            date_part = DateUtils.addMinutes(date_part, calendar.get(Calendar.MINUTE));
            date_part = DateUtils.addSeconds(date_part, calendar.get(Calendar.SECOND));

        }else if(item.equals("To")){
            // dateTill 0:0:0
            date_part.setTime(date_part.getTime() + 86340000); // + 23ч 59м 00c 000мс
        }
        return date_part;
    }
}
