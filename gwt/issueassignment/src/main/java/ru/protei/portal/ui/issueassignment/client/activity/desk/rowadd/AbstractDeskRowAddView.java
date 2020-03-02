package ru.protei.portal.ui.issueassignment.client.activity.desk.rowadd;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractDeskRowAddView extends IsWidget {

    void setHandler(Handler handler);

    void setButtonTitle(String title);

    interface Handler {
        void onAdd();
    }
}
