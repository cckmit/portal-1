package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class DutyLogEvents {

    @Url(value = "duty_logs", primary = true)
    public static class Show {}

    public static class Edit {
        public Long id;

        public Edit() {}

        public Edit (Long id) {
            this.id = id;
        }
    }

    public static class Update {}

    public static class CreateReport {}
}
