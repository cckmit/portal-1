package ru.protei.portal.ui.common.client.activity.caselink.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseLink;

/**
 * Представление линка
 */
public interface AbstractCaseLinkItemView extends IsWidget {

    void setActivity(AbstractCaseLinkItemActivity activity);

    void setValue(CaseLink value);

    CaseLink getValue();

    void setEnabled(boolean enabled);
}
