package ru.protei.portal.ui.project.client.activity.list.item;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

public interface AbstractProjectDocumentsListItemView extends IsWidget {

    void setActivity(AbstractProjectDocumentsListItemActivity activity);

    void setApproved(Boolean isApproved);

    void setDecimalNumber(String decimalNumber);

    void setInfo(String info);

    void setEditVisible(boolean visible);
}
