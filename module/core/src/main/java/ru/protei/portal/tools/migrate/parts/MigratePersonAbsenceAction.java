package ru.protei.portal.tools.migrate.parts;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.tools.migrate.tools.BatchProcess;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.winter.jdbc.JdbcDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 01.04.16.
 */
public class MigratePersonAbsenceAction implements MigrateAction {

    public static final String TM_PERSON_ABS_ITEM_CODE = "ABSENCE-LOG";
    public static final String TM_PERSON_LEAVE_ITEM_CODE = "LEAVE-LOG";
    private Calendar calendar = Calendar.getInstance();

    @Autowired
    PersonAbsenceDAO dao;

    @Autowired
    private MigrationEntryDAO migrateDAO;

    @Override
    public int orderOfExec() {
        return 5;
    }

//    static { // ДЛЯ ПРОВЕРКИ
//        try {
//            System.setProperty("java.util.logging.config.file", "/home/bondarenko/portal/module/web-ui/src/main/java/ru/protei/portal/webui/controller/dict/logging.properties");
//            LogManager.getLogManager().readConfiguration();
//        }catch (Throwable e){
//            System.out.println(e);
//        }
//    }


    @Override
    public void migrate(Connection src) throws SQLException {


        BatchProcess<PersonAbsence> batchProcess = new BaseBatchProcess<PersonAbsence>() {
            @Override
            protected void processUpdate(JdbcDAO<Long, PersonAbsence> dao, List<PersonAbsence> entries) {
                for(PersonAbsence e : entries){
                    dao.mergeByCondition(e, "old_id=?", e.getOldId());
                }
            }
        };


        new BatchProcessTaskExt(migrateDAO, TM_PERSON_ABS_ITEM_CODE)
                .forTable("\"AbsentLog\".Tm_AbsentViewer", "nID", "dtLastUpdate")
                .process(src, dao, batchProcess, row -> {
                    PersonAbsence x = new PersonAbsence();
                    x.setOldId(((Number) row.get("nID")).longValue());
                    x.setCreated((Date) row.get("dtCreation"));
                    x.setUpdated(x.getCreated());
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
                .dumpStats();


        new BatchProcessTaskExt(migrateDAO, TM_PERSON_LEAVE_ITEM_CODE)
                .forTable("\"AbsentLog\".Tm_Leave", "nID", "dtLastUpdate")
                .process(src, dao, batchProcess, row -> {
                    PersonAbsence x = new PersonAbsence();
                    x.setOldId(((Number) row.get("nID")).longValue());
                    x.setCreated((Date) row.get("dtCreation"));
                    x.setUpdated(x.getCreated());
                    x.setCreatorId(((Number) row.get("nSubmitterID")).longValue());
                    x.setPersonId(((Number) row.get("nPersonID")).longValue());
                    x.setUserComment((String) row.get("strComment"));
                    x.setReasonId(2); // отпуск

                    Date dateFrom = (Date) row.get("dFromDate");
                    Date dateTill = (Date) row.get("dToDate");
                    dateTill.setTime(dateTill.getTime() + 86340000); // + 23ч 59м 00c 000мс

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
                .dumpStats();

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
