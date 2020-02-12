package ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.issue;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;

public interface AbstractDeskIssueView extends IsWidget {

    void setHandler(Handler handler);

    void setImportance(En_ImportanceLevel importance);

    void setState(En_CaseState state);

    void setPrivacy(boolean isPrivate);

    void setNumber(long number);

    void setName(String name);

    void setInitiatorCompany(String initiatorCompany);

    void setInitiatorName(String initiatorName);

    void setProduct(String product);

    void setCreated(String created);

    void setModified(String modified);

    interface Handler {
        void onOpen();
        void onOptions();
    }
}
