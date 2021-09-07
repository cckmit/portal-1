package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;


public class YoutrackReportDictionaryEvents {
    public static class Show {
        public En_ReportYoutrackWorkType type;

        public Show(En_ReportYoutrackWorkType type) {
            this.type = type;
        }
    }

    public static class Changed {
        public En_ReportYoutrackWorkType type;

        public Changed(En_ReportYoutrackWorkType type) {
            this.type = type;
        }
    }
}
