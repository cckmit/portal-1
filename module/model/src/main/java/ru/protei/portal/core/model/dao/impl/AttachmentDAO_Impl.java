package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import java.util.List;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

/**
 * Created by bondarenko on 26.01.17.
 */
public class AttachmentDAO_Impl extends PortalBaseJdbcDAO<Attachment> implements AttachmentDAO {

    @Override
    public List<Attachment> getAttachmentsByCaseId(Long caseId) {
        Query query = query()
                .where("attachment.id").in(
                        getSelectConditionByCaseId("case_attachment.att_id", caseId).asQuery()
                ).asQuery();

        return getListByCondition(query.buildSql(), query.args());
    }

    @Override
    public List<Attachment> getPublicAttachmentsByCaseId(Long caseId) {
        Query query = query()
                .where("attachment.id").in(
                        getSelectConditionByCaseId("case_attachment.att_id", caseId)
                                .and(getPublicCondition())
                                .asQuery()
                ).asQuery();

        return getListByCondition(query.buildSql(), query.args());
    }

    @Override
    public boolean hasPublicAttachments(Long caseId) {
        Query query = getSelectConditionByCaseId("count(*)", caseId)
                .and(getPublicCondition())
                .asQuery();

        return jdbcTemplate.queryForObject(query.buildSql(), query.args(), Integer.class) > 0;
    }

    private Condition getPublicCondition() {
        return condition()
                .or("case_attachment.ccomment_id").isNull(true)
                .or("case_attachment.ccomment_id").in(
                        query()
                                .select("case_comment.id")
                                .from("case_comment")
                                .where("case_comment.private_flag").equal(false)
                                .asQuery()
                );
    }

    private Condition getSelectConditionByCaseId(String selectExpression, Long caseId) {
        return query()
                .select(selectExpression)
                .from("case_attachment")
                .where("case_attachment.case_id").equal(caseId);
    }
}
