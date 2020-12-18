package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dto.CaseResolutionTimeReportDto;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.HistoryQuery;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface HistoryDAO extends PortalBaseDAO<History> {

    List<History> getListByQuery(HistoryQuery query);

    void removeByCaseId(Long caseId);

    List<CaseResolutionTimeReportDto> reportCaseResolutionTime(Date from, Date to, List<Long> terminatedStates,
                                                               List<Long> companiesIds, Set<Long> productIds, List<Long> managersIds, List<Integer> importanceIds,
                                                               List<Long> tagsIds);
}
