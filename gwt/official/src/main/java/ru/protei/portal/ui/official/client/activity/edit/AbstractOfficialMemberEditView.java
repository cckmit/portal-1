package ru.protei.portal.ui.official.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Абстрактное представление формы редактирования должностного лица
 */
public interface AbstractOfficialMemberEditView extends IsWidget {

    HasValue<String> lastName();

    HasValue<String> firstName();

    HasValue<String> secondName();

    HasValue<EntityOption> organization();

    HasValue<String> position();

    HasValue<String> relations();

    HasValue<String> amplua();

    void setActivity(AbstractOfficialMemberEditActivity activity);
}
