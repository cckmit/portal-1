package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class EducationEvents {

    @Url(value = "education", primary = true)
    public static class Show {
        public Show () {}
    }

}
