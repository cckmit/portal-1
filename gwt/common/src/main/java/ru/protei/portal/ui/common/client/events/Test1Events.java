package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class Test1Events {

    @Url(value = "test1", primary = true)
    public static class Show {
        public Show() {
        }
    }
}
