package ru.protei.portal.core.model.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.HistoryDAO;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.CaseResolutionTimeReportDto;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.HistoryQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.enums.HasId;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class HistoryDAO_Impl extends PortalBaseJdbcDAO<History> implements HistoryDAO {

    @Override
    public List<History> getListByQuery(HistoryQuery query) {
        if (query.getSortDir() == null) {
            query.setSortField(En_SortField.id);
            query.setSortDir(En_SortDir.ASC);
        }

        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query))
        );
    }

    @Override
    public History getLastHistory(Long caseObjectId, En_HistoryType historyType) {
        Condition condition = query()
                .where("history.case_object_id").equal(caseObjectId)
                .and("history.value_type").equal(historyType.getId());

        List<History> historyResult = getSearchResult(new JdbcQueryParameters()
                .withCondition(condition.getSqlCondition(), condition.getSqlParameters())
                .withSort(new JdbcSort(JdbcSort.Direction.DESC, "history.date"))
                .withLimit(1)
        ).getResults();

        return getFirst(historyResult);
    }

    @Override
    public void removeByCaseId(Long caseId) {
        Condition condition = condition().and("case_object_id").equal(caseId);
        removeByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }

    @Override
    public List<CaseResolutionTimeReportDto> reportCaseResolutionTime(Date from, Date to, List<Long> terminatedStates,
                                                                      List<Long> companiesIds, Set<Long> productIds, List<Long> managersIds, List<Integer> importanceIds,
                                                                      List<Long> tagsIds) {
        String fromTime = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( from );
        String toTime = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( to );
        String acceptableStates = makeInArg( terminatedStates, false);

        String products = "";
        if ( productIds != null && !productIds.isEmpty() ) {
            if (productIds.remove(CrmConstants.Product.UNDEFINED)) {
                products += " and (ob.product_id is null";
                if (!productIds.isEmpty()) {
                    products += " or ob.product_id in " + makeInArg(productIds, false);
                }
                products += ")";
            } else {
                products += " and ob.product_id in " + makeInArg(productIds, false);
            }
        }

        String companies = makeAndPartFromListIds(companiesIds, "ob.initiator_company");
        String managers = makeAndPartFromListIds(managersIds, "ob.manager");
        String importance = makeAndPartFromListIds(importanceIds, "ob.importance");
        String tags = tagsIds == null ? "" : " and ob.ID in (SELECT cot.case_id FROM case_object_tag cot WHERE cot.tag_id in " + makeInArg(tagsIds, false) + ")";

        // Активные задачи на момент начала интервала запроса
        String activeCasesAtIntervalStart =
                "SELECT case_object_id, h.date, new_id" +
                        " FROM history h" +
                        "        LEFT OUTER JOIN case_object ob on ob.id = h.case_object_id" +
                        " WHERE h.date = (" +
                        "   SELECT max(subQuery.date) last" +
                        "   FROM history subQuery" +
                        "   WHERE case_object_id = h.case_object_id" +
                        "     and date < '" + fromTime + "'" +  // # левая граница
                        "     and value_type = " + En_HistoryType.CASE_STATE.getId() +
                        "     and new_id is not null" +
                        " )" +
                        "   and new_id in " + acceptableStates +
                        "   and value_type = " + En_HistoryType.CASE_STATE.getId()
                        + products
                        + companies
                        + managers
                        + importance
                        + tags
                ;

        // Задачи переходящие в активное состояние в интервале запроса
        String activeCasesInInterval =
                "SELECT case_object_id, h.date, new_id" +
                        " FROM history h" +
                        "        LEFT OUTER JOIN case_object ob on ob.id = h.case_object_id" +
                        " WHERE h.date > '" + fromTime + "'" +  // # левая граница
                        "   and h.date < '" + toTime + "' " +  //# правая граница
                        "   and h.value_type = " + En_HistoryType.CASE_STATE.getId() +
                        "   and h.new_id in " + acceptableStates
                        + products
                        + companies
                        + managers
                        + importance
                        + tags
                ;

        String query =
                "SELECT case_object_id, outerH.date as historyCreated, new_id as stateId, ob.CASENO as caseObjectNumber" +
                        " FROM history outerH LEFT JOIN case_object ob on ob.id = outerH.case_object_id" +
                        " WHERE outerH.case_object_id in (" +
                        "   SELECT DISTINCT case_object_id" +
                        "   from (" +
                        activeCasesAtIntervalStart +
                        " union " +
                        activeCasesInInterval +
                        "        ) as beforeAndInInterval " +
                        " )" +
                        " and outerH.date < '" + toTime + "' " + //# правая граница
                        " and outerH.value_type = " + En_HistoryType.CASE_STATE.getId() +
                        " ORDER BY outerH.date ASC;";

        try {
            return jdbcTemplate.query( query, rm );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    RowMapper<CaseResolutionTimeReportDto> rm = (r, i) -> {
        CaseResolutionTimeReportDto comment = new CaseResolutionTimeReportDto();

        comment.setCaseId( r.getLong( "case_object_id" ) );
        comment.setCaseNumber(r.getLong( "caseObjectNumber" ));
        Long cstateId = r.getLong( "stateId" );
        comment.setCaseStateId( r.wasNull() ? null : cstateId );
        comment.setCreated( new Date( r.getTimestamp( "historyCreated" ).getTime() ) );

        return comment;
    };

    private String makeAndPartFromListIds(final List<?> list, final String field){
        return list == null ? "" : " and " + field + " in " + makeInArg(list, false);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(HistoryQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (query.getInitiatorId() != null) {
                condition.append(" and history.initiator_id = ?");
                args.add(query.getInitiatorId());
            }

            if (query.getDateFrom() != null) {
                condition.append(" and history.date >= ?");
                args.add(query.getDateFrom());
            }

            if (query.getDateTo() != null) {
                condition.append(" and history.date <= ?");
                args.add(query.getDateTo());
            }

            if (query.getCaseObjectId() != null) {
                condition.append(" and history.case_object_id = ?");
                args.add(query.getCaseObjectId());
            }

            if (query.getCaseNumber() != null) {
                condition.append(" and history.case_object_id in (select case_object.ID from case_object where case_object.CASENO = ?");
                args.add(query.getCaseNumber());
            }

            if (isNotEmpty(query.getValueTypes())) {
                condition.append(" and history.value_type in ").append(makeInArg(makeListIds(query.getValueTypes()), false));
            }

            if (isNotEmpty(query.getHistoryActions())) {
                condition.append(" and history.action_type in ").append(makeInArg(makeListIds(query.getHistoryActions()), false));
            }

            if (query.getOldId() != null) {
                condition.append(" and history.old_id = ?");
                args.add(query.getOldId());
            }

            if (query.getNewId() != null) {
                condition.append(" and history.new_id = ?");
                args.add(query.getNewId());
            }

        }));
    }

    private <T extends HasId> List<Integer> makeListIds(List<T> list) {
        return stream(list).map(HasId::getId).collect(Collectors.toList());
    }
}
