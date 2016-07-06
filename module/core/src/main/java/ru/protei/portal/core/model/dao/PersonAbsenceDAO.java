package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 05.07.16.
 */
public interface PersonAbsenceDAO extends PortalBaseDAO<PersonAbsence> {

    public List<PersonAbsence> getForRange (Long person, Date from, Date till);
}
