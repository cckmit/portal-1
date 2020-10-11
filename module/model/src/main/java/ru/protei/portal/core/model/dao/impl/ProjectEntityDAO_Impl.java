package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ProjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

import java.util.Collection;

import static ru.protei.portal.core.model.dto.Project.Columns.*;

public class ProjectEntityDAO_Impl extends PortalBaseJdbcDAO<Project> implements ProjectDAO {

    @Override
    public Collection<Project> selectScheduledPauseTime( long greaterThanTime ) {
        Condition condition = SqlQueryBuilder.condition()
                .and( CASE_TYPE ).equal( En_CaseType.PROJECT.getId() )
                .and( DELETED ).equal( Project.NOT_DELETED )
                .and( PAUSE_DATE ).gt( greaterThanTime );
        return partialGetListByCondition( condition.getSqlCondition(), condition.getSqlParameters(), ID, PAUSE_DATE );
    }

    @Override
    public Collection<Project> getListByCompanyId( long companyId ) {
        Condition condition = SqlQueryBuilder.condition()
                .and( CASE_TYPE ).equal( En_CaseType.PROJECT.getId() )
                .and( DELETED ).equal( Project.NOT_DELETED )
                .and( COMPANY ).equal( companyId );
        return partialGetListByCondition( condition.getSqlCondition(), condition.getSqlParameters(), ID, NAME );
    }
}
