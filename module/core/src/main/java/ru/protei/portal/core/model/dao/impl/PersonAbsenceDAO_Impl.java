package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.time.DateUtils;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 05.07.16.
 */
public class PersonAbsenceDAO_Impl extends PortalBaseJdbcDAO<PersonAbsence> implements PersonAbsenceDAO {

    public List<PersonAbsence> getForRange (Long personId, Date from, Date till) {
        if (from == null) {
            from = DateUtils.addDays(new Date(), -10);
        }

        if (till == null) {
            till = DateUtils.addDays(from, 50);
        }

        return getListByCondition("person_id=? and from_time >= ? and from_time < ?", personId, from, till);
    }
}
