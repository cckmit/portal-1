package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.YtWorkQuery;

public class ReportYtWorkQuery implements ReportDto {

    private Report report;
    private YtWorkQuery query;

    public ReportYtWorkQuery() {
    }

    public ReportYtWorkQuery(Report report, YtWorkQuery query) {
        this.report = report;
        this.query = query;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public YtWorkQuery getQuery() {
        return query;
    }

    public void setQuery(YtWorkQuery query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "ReportYtWorkQuery{" +
                "report=" + report +
                ", query=" + query +
                '}';
    }
}
