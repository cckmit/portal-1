package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ProjectTechnicalSupportValidityReportInfoDAO;
import ru.protei.portal.core.model.dto.ProjectTSVReportInfo;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

public class ProjectTechnicalSupportValidityReportInfoDAO_Impl
        extends PortalBaseJdbcDAO<ProjectTSVReportInfo>
        implements ProjectTechnicalSupportValidityReportInfoDAO {

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ProjectQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append(" 1=1 ");
            if (!isEmpty(query.getTechnicalSupportExpiresInDays())) {
                condition.append(
                        query.getTechnicalSupportExpiresInDays().stream().map(interval -> {
                            args.add(interval.getFrom());
                            args.add(interval.getTo());
                            return "(project.technical_support_validity >= ? and project.technical_support_validity <= ?)";
                        }).collect(Collectors.joining(" or ", " and( ", " ) ")));
            }
        }));
    }
}
