package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ProjectEntityDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.struct.ProjectEntity;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

import java.util.Collection;

import static ru.protei.portal.core.model.struct.ProjectEntity.Columns.*;

public class ProjectEntityDAO_Impl extends PortalBaseJdbcDAO<ProjectEntity> implements ProjectEntityDAO {


    @Override
    public Collection<ProjectEntity> selectScheduledPauseTime( long greaterThanTime ) {
        Condition condition = SqlQueryBuilder.condition()
                .and( CASE_TYPE ).equal( En_CaseType.PROJECT.getId() )
                .and( DELETED ).equal( ProjectEntity.NOT_DELETED )
                .and( PAUSE_DATE ).gt( greaterThanTime );
        return partialGetListByCondition( condition.getSqlCondition(), condition.getSqlParameters(), ID, PAUSE_DATE );
    }
}
