package ru.protei.portal.ui.crm.client.events;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * Created by turik on 23.09.16.
 */
public class AuthEvents {

    //@Url( value = "login", primary = true )
    public static class Show {
        public Show() {}

        public Show(HasWidgets parent) {
            this.parent = parent;
        }

        public HasWidgets parent;
    }

    public static class Success {

        public Success(String userName) { this.userName = userName; }

        public String userName;
    }
}
