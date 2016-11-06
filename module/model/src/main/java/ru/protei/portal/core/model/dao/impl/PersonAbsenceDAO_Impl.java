package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.time.DateUtils;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.winter.jdbc.JdbcHelper;
import ru.protei.winter.jdbc.annotations.JdbcColumn;

import java.util.*;

/**
 * Created by michael on 05.07.16.
 */
public class PersonAbsenceDAO_Impl extends PortalBaseJdbcDAO<PersonAbsence> implements PersonAbsenceDAO {

    private static Calendar calendar = Calendar.getInstance();

    private Date from;
    private Date till;


    public List<PersonAbsence> getForRange (Long personId, Date from, Date till) {

        if(from == null || till == null){
            Date today = new Date();
            calendar.setTime(today);

            int days = 60; // всего
            int daysBefore = 14 + calendar.get(Calendar.DAY_OF_WEEK) - 1; // 14 дней + дней до предыдущего понедельника
            from = DateUtils.addDays(today, -daysBefore);
            till = DateUtils.addDays(from, days);
        }

        //getListByCondition("person_id=? and from_time >= ? and from_time < ?", personId, from, till); // Не корректно, т.к если начало отсутствия ИЛИ конец отсутствия выходят за границы диапазона, то такое отсутствие не будет включено
        return getListByCondition("person_id=? and from_time < ? and till_time > ?", personId, till, from);
    }

    public List<PersonAbsence> getCurrentAbsences (Date now) {
        if (now == null)
            now = new Date();

        List<Object> list = new ArrayList<>();
        list.add(now);
        list.add(now);


        /**
         * Review notes:
         *
         * to get ID column name use : getIdColumnName()
         * to get reference of mapped entity class use : getObjectMapper().getEntityClass()
         */
//        String personIdColumn;
//        try {
//            personIdColumn = PersonAbsence.class.getField("personId").getAnnotation(JdbcColumn.class).name();
//        }catch (NoSuchFieldException e){
//            personIdColumn = "person_id";
//        }

        return getListByCondition("from_time < ? and till_time > ?", list);
    }
}
