package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseObjectTagDAO;
import ru.protei.portal.core.model.ent.CaseObjectTag;
import ru.protei.portal.core.model.util.sqlcondition.Condition;

import java.util.List;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class CaseObjectTagDAO_Impl extends PortalBaseJdbcDAO<CaseObjectTag> implements CaseObjectTagDAO {

    @Override
    public int removeByCaseIdAndTagId(Long caseId, Long tagId) {
        return removeByCondition("case_id = ? and tag_id = ?", caseId, tagId);
    }

    @Override
    public boolean checkExists(Long caseId, Long tagId) {
        return checkExistsByCondition("case_id = ? and tag_id = ?", caseId, tagId);
    }

    @Override
    public List<CaseObjectTag> list(List<Long> caseIds, List<Long> tagIds, List<Long> companyIds) {
        Condition condition = query()
                .where("case_object_tag.case_id").in(caseIds)
                .and("case_object_tag.tag_id").in(tagIds);
        if (companyIds != null) {
            condition
                .and("case_object_tag.tag_id").in(query()
                    .select("case_tag.id")
                    .from("case_tag")
                    .where("case_tag.company_id").in(companyIds)
                    .asQuery()
                );
        }
        return getListByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }
}
