package ru.protei.portal.ui.official.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.OfficialMember;

/**
 * Абстрактное представление должностного лица
 */
public interface AbstractOfficialItemView extends IsWidget {

    void setName(String firstName);

    void setAmplua(String amplua);

    void setPosition(String position);

    void setRelations(String relations);
}
