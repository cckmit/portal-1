package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.time.DateUtils;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by michael on 05.07.16.
 */
public class PersonAbsenceDAO_Impl extends PortalBaseJdbcDAO<PersonAbsence> implements PersonAbsenceDAO {

    private static Calendar calendar = Calendar.getInstance();

    public List<PersonAbsence> getForRange (Long personId, Date from, Date till) {

        if(from == null || till == null){
            Date today = new Date();
            calendar.setTime(today);

            int days = 60; // всего
            int daysBefore = 14 + calendar.get(Calendar.DAY_OF_WEEK) - 1; // 14 дней + дней до предыдущего понедельника
            from = DateUtils.addDays(today, -daysBefore);
            till = DateUtils.addDays(from, days - daysBefore);
        }

        return getListByCondition("person_id=? and from_time >= ? and from_time < ?", personId, from, till);
    }
}
