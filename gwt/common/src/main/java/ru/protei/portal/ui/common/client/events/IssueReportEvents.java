package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class IssueReportEvents {

    @Url(value = "issue_reports", primary = true)
    public static class Show {
        public Show () {}
    }

    @Url(value = "issue_report")
    public static class Edit {

        public Edit () {
            this.reportId = null;
        }
        public Edit (Long reportId) {
            this.reportId = reportId;
        }

        public Long reportId;
    }
}
