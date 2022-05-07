package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.TransportationRequestQuery;

public class ReportTransportationRequestQuery implements ReportDto {

    private Report report;
    private TransportationRequestQuery query;

    public ReportTransportationRequestQuery() {
    }

    public ReportTransportationRequestQuery(Report report, TransportationRequestQuery query) {
        this.report = report;
        this.query = query;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public TransportationRequestQuery getQuery() {
        return query;
    }

    public void setQuery(TransportationRequestQuery query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "ReportTransportationRequestQuery{" +
                "report=" + report +
                ", query=" + query +
                '}';
    }
}
