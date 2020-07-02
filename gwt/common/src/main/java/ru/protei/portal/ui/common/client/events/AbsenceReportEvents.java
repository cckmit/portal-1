package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class AbsenceReportEvents {
    @Url(value = "absence_reports", primary = true)
    public static class Show {
        public Show () {}
    }
}
