package ru.protei.portal.ui.common.client.activity.casetag.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;

/**
 * Представление тега
 */
public interface AbstractCaseTagItemView extends IsWidget {

    void setActivity(AbstractCaseTagItemActivity activity);

    void setEnabled(boolean enabled);

    void setNameAndColor(String name, String color);

    void setModelId(Long id);

    Long getModelId();
}
