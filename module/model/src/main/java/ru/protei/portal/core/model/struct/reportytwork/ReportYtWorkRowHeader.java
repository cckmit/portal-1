package ru.protei.portal.core.model.struct.reportytwork;

public final class ReportYtWorkRowHeader implements ReportYtWorkRow {
    final int level;
    final String value;

    public ReportYtWorkRowHeader(int level, String value) {
        this.level = level;
        this.value = value;
    }

    public int getLevel() {
        return level;
    }

    public String getValue() {
        return value;
    }
}
