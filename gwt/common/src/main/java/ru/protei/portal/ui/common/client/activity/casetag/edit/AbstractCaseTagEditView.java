package ru.protei.portal.ui.common.client.activity.casetag.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;

public interface AbstractCaseTagEditView extends IsWidget {

    void setActivity(AbstractCaseTagEditActivity activity);

    HasValue<String> name();

    HasValue<String> color();

    HasValue<EntityOption> company();

    HasEnabled colorEnabled();

    HasEnabled nameEnabled();

    HasEnabled companyEnabled();

    HasVisibility authorVisibility();

    void setAuthor(String author);
}