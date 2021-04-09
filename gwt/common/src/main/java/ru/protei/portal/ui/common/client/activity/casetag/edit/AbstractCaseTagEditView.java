package ru.protei.portal.ui.common.client.activity.casetag.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;

public interface AbstractCaseTagEditView extends IsWidget {

    void setActivity(AbstractCaseTagEditActivity activity);

    HasValue<String> name();

    void setCaseTagNameStatus(NameStatus status);

    HasText caseTagNameErrorLabel();

    HasVisibility caseTagNameErrorLabelVisibility();

    HasValue<String> color();

    HasValue<EntityOption> company();

    HasEnabled colorEnabled();

    HasEnabled nameEnabled();

    HasEnabled companyEnabled();

    HasVisibility authorVisibility();

    void setAuthor(String author);
}
