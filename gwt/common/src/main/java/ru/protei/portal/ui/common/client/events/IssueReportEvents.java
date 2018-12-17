package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class IssueReportEvents {

    @Url(value = "issue_reports", primary = true)
    public static class Show {
        public Show () {}
    }

    public static class Create {
        public Create () {}
    }
}
