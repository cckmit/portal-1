package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.EducationEntry;

public class EducationEvents {

    @Url(value = "education", primary = true)
    public static class Show {
        public Show () {}
    }

    @Url(value = "education_entry")
    public static class EditEducationEntry {
        @Omit public EducationEntry entry;
        @Omit public HasWidgets parent;
        public EditEducationEntry() {}
        public EditEducationEntry(EducationEntry entry) {
            this.entry = entry;
        }
        public EditEducationEntry(HasWidgets parent, EducationEntry entry) {
            this.parent = parent;
            this.entry = entry;
        }
    }

    public static class ShowWorker {
        public HasWidgets parent;
        public ShowWorker() {}
        public ShowWorker(HasWidgets parent) {
            this.parent = parent;
        }
    }

    public static class ShowWorkerTable {
        public HasWidgets parent;
        public ShowWorkerTable() {}
        public ShowWorkerTable(HasWidgets parent) {
            this.parent = parent;
        }
    }

    public static class ShowAdmin {
        public HasWidgets parent;
        public ShowAdmin() {}
        public ShowAdmin(HasWidgets parent) {
            this.parent = parent;
        }
    }
}
