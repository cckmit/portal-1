package ru.protei.portal.ui.common.client.activity.caselink.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;

/**
 * Представление линка
 */
public interface AbstractCaseLinkItemView extends IsWidget {

    void setActivity(AbstractCaseLinkItemActivity activity);

    void setEnabled(boolean enabled);

    void setHref(String link);

    void setNumber(String value);

    void setName(String value);

    void setState(En_CaseState value);

    void setModelId(Long id);

    Long getModelId();
}
