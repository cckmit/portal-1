package ru.protei.portal.ui.common.client.activity.casetag.list;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseType;

/**
 * Представление списка тегов
 */
public interface AbstractCaseTagListView extends IsWidget {

    void setActivity(AbstractCaseTagListActivity activity);

    void showSelector(IsWidget target);

    HasWidgets getTagsContainer();

    HasVisibility getTagsContainerVisibility();

    void setType(En_CaseType type);

    void setTagsAddButtonEnabled(boolean enabled);

    void setTagsEditButtonEnabled(boolean enabled);

    boolean isAttached();
}
