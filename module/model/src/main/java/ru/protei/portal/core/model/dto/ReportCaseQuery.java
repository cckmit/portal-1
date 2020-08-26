package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;

public class ReportCaseQuery implements ReportDto {

    private Report report;
    private CaseQuery query;

    public ReportCaseQuery() {
    }

    public ReportCaseQuery(Report report, CaseQuery query) {
        this.report = report;
        this.query = query;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public CaseQuery getQuery() {
        return query;
    }

    public void setQuery(CaseQuery query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "ReportCaseQuery{" +
                "report=" + report +
                ", query=" + query +
                '}';
    }
}
