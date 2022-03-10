package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.YoutrackWorkQuery;

public class ReportYoutrackWorkQuery implements ReportDto {

    private Report report;
    private YoutrackWorkQuery query;

    public ReportYoutrackWorkQuery() {
    }

    public ReportYoutrackWorkQuery(Report report, YoutrackWorkQuery query) {
        this.report = report;
        this.query = query;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public YoutrackWorkQuery getQuery() {
        return query;
    }

    public void setQuery(YoutrackWorkQuery query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "ReportYoutrackWorkQuery{" +
                "report=" + report +
                ", query=" + query +
                '}';
    }
}
