package ru.protei.portal.core.model.struct.reportytwork;

public class ReportYtWorkRowHeader implements ReportYtWorkRow {
    final String value;

    public ReportYtWorkRowHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
