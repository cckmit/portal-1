package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ProjectTechnicalSupportValidityReportInfoDAO;
import ru.protei.portal.core.model.dto.ProjectTSVReportInfo;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class ProjectTechnicalSupportValidityReportInfoDAO_Impl
        extends PortalBaseJdbcDAO<ProjectTSVReportInfo>
        implements ProjectTechnicalSupportValidityReportInfoDAO {

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ProjectQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            if (query.getExpiringTechnicalSupportValidityFrom() != null) {
                condition.append( "project.technical_support_validity  >= ?" );
                args.add( query.getExpiringTechnicalSupportValidityFrom() );
            }

            if (query.getExpiringTechnicalSupportValidityTo() != null) {
                condition.append( " and project.technical_support_validity <= ?" );
                args.add( query.getExpiringTechnicalSupportValidityTo() );
            }
        }));
    }
}
