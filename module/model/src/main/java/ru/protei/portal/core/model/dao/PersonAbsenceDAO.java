package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 05.07.16.
 */
public interface PersonAbsenceDAO extends PortalBaseDAO<PersonAbsence> {

    List<PersonAbsence> listByEmployeeAndDateBounds(Long absenceId, Date from, Date till);

    public List<PersonAbsence> getForRange (Long person, Date from, Date till);

    public List<PersonAbsence> getCurrentAbsences (Date now);
}
