package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;

import java.util.List;

public class CaseFilterDAO_Impl extends PortalBaseJdbcDAO< CaseFilter > implements CaseFilterDAO {

    @Override
    public List< CaseFilter > getListByLoginIdAndFilterType( Long loginId, En_CaseFilterType filterType ) {
        return getListByCondition( "login_id=? and type=?", loginId, filterType.name() );
    }

    @Override
    public CaseFilter checkExistsByParams( String name, Long loginId, En_CaseFilterType type ) {
        return getByCondition("name=? and login_id=? and type=?", name, loginId, type.name() );
    }
}
