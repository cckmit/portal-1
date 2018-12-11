package ru.protei.portal.core.service.report.managertime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentCaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseCommentCaseObject;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ReportCrmManagerTimeServiceImpl implements ReportCrmManagerTimeService {

    private static Logger log = LoggerFactory.getLogger(ReportCrmManagerTimeServiceImpl.class);

    @Autowired
    Lang lang;
    @Autowired
    CaseCommentCaseObjectDAO caseCommentCaseObjectDAO;

    @Override
    public boolean writeExport(ByteArrayOutputStream buffer, Report report) throws IOException {

        CaseQuery caseQuery = report.getCaseQuery();
        CaseCommentQuery caseCommentQuery = new CaseCommentQuery(); // place CaseCommentQuery to Report
        caseCommentQuery.useSort(En_SortField.person_id, En_SortDir.DESC);
        Long count = caseCommentCaseObjectDAO.count(caseQuery, caseCommentQuery);

        if (count == null || count < 1) {
            log.debug("writeReport : reportId={} has no corresponding case comments", report.getId());
            return true;
        }

        if (count > Integer.MAX_VALUE) {
            log.debug("writeReport : reportId={} has too many corresponding case comments: {}, aborting task", report.getId(), count);
            return false;
        }

        log.debug("writeReport : reportId={} has {} case comments to procees", report.getId(), count);

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));

//        CaseQuery caseQuery = new CaseQuery();
//        caseQuery.setStates(Collections.singletonList(En_CaseState.CREATED));
//        CaseCommentQuery caseCommentQuery = new CaseCommentQuery();
//        List<CaseCommentCaseObject> cases = caseCommentCaseObjectDAO.getListByQueries(caseQuery, caseCommentQuery);
//        cases.size();

        return false;
    }
}
