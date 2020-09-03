package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ContractQuery;

public class ReportContractQuery implements ReportDto {

    private Report report;
    private ContractQuery query;

    public ReportContractQuery() {
    }

    public ReportContractQuery(Report report, ContractQuery query) {
        this.report = report;
        this.query = query;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public ContractQuery getQuery() {
        return query;
    }

    public void setQuery(ContractQuery query) {
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
