package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;

public class EducationEvents {

    @Url(value = "education", primary = true)
    public static class Show {
        public Show () {}
    }

    public static class ShowWorker {
        public HasWidgets parent;
        public ShowWorker() {}
        public ShowWorker(HasWidgets parent) {
            this.parent = parent;
        }
    }

}
