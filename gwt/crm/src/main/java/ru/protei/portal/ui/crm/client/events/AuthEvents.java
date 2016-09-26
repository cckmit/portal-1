package ru.protei.portal.ui.crm.client.events;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * Created by turik on 23.09.16.
 */
public class AuthEvents {
    public static class Show {
        public Show(HasWidgets parent) {
            this.parent = parent;
        }

        public HasWidgets parent;
    }
}
