package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.dto.ReportDto;

public class ReportEvents {

    @Url(value = "reports", primary = true)
    public static class Show {
        public Show () {}
    }

    @Url(value = "new_report")
    public static class Create {
        public Create () {}
    }

    @Url(value = "edit_report")
    public static class Edit {
        @Name( "id" )
        public Long reportId;
        @Omit
        public ReportDto reportDto;

        public Edit() {
        }

        public Edit(Long reportId, ReportDto reportDto) {
            this.reportId = reportId;
            this.reportDto = reportDto;
        }
    }
}
