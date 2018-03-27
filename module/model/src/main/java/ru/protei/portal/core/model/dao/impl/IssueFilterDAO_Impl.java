package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.IssueFilterDAO;
import ru.protei.portal.core.model.ent.IssueFilter;

import java.util.List;

public class IssueFilterDAO_Impl extends PortalBaseJdbcDAO<IssueFilter> implements IssueFilterDAO {

    @Override
    public List< IssueFilter > getFiltersByUser( Long loginId ) {

        return getListByCondition("login_id=?", loginId);
    }
}
