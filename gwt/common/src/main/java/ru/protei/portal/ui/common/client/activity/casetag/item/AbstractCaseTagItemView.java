package ru.protei.portal.ui.common.client.activity.casetag.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseTag;

/**
 * Представление тега
 */
public interface AbstractCaseTagItemView extends IsWidget {

    void setActivity(AbstractCaseTagItemActivity activity);

    void setEnabled(boolean enabled);

    void setNameAndColor(String name, String color);

    void setCaseTag( CaseTag caseTag);

    CaseTag getCaseTag();
}
