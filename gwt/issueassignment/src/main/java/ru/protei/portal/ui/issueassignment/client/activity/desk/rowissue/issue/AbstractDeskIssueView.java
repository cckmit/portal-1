package ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.issue;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;

public interface AbstractDeskIssueView extends IsWidget {

    void setHandler(Handler handler);

    void setWarningHighlight();

    void setImportance(String importanceCode);

    void setState(String state);

    void setPrivacy(boolean isPrivate);

    void setNumber(long number);

    void setName(String name);

    void setInitiatorCompany(String initiatorCompany);

    void setInitiatorName(String initiatorName);

    void setProduct(String product);

    void setCreated(String created);

    void setModified(String modified);

    HasWidgets getTagsContainer();

    interface Handler {
        void onOpen();
        void onOptions(UIObject relative);
    }
}
