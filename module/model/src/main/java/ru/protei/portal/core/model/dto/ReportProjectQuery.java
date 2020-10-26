package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ProjectQuery;

public class ReportProjectQuery implements ReportDto {

    private Report report;
    private ProjectQuery query;

    public ReportProjectQuery() {
    }

    public ReportProjectQuery(Report report, ProjectQuery query) {
        this.report = report;
        this.query = query;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public ProjectQuery getQuery() {
        return query;
    }

    public void setQuery(ProjectQuery query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "ReportContractQuery{" +
                "report=" + report +
                ", query=" + query +
                '}';
    }
}
