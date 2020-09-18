package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class ReportEvents {

    @Url(value = "reports", primary = true)
    public static class Show {
        public Show () {}
    }

    @Url(value = "new_report")
    public static class Create {
        public Create () {}
    }
}
