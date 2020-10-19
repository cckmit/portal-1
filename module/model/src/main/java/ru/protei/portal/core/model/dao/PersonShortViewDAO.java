package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.JdbcDAO;

import java.util.List;

public interface PersonShortViewDAO extends PortalBaseDAO<PersonShortView> {

    List<PersonShortView> getPersonsShortView( PersonQuery query );

}
