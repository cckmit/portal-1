package ru.protei.portal.ui.common.client.events;


import com.google.gwt.user.client.ui.HasWidgets;

public class ModuleEvents {

    public static class Show {
        public Show (HasWidgets parent, Long id) {
            this.parent = parent;
            this.id = id;
        }

        public Long id;
        public HasWidgets parent;
    }
}
