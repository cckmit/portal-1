package ru.protei.portal.ui.common.client.activity.casetag.list;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseType;

/**
 * Представление списка тегов
 */
public interface AbstractCaseTagListView extends IsWidget {

    void setActivity(AbstractCaseTagListActivity activity);

    HasWidgets getTagsContainer();

    HasVisibility addButtonVisibility();

    void setType(En_CaseType type);

    void setTagsAddButtonEnabled(boolean enabled);

    void setTagsEditButtonEnabled(boolean enabled);
}
