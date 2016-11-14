package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget {

    void setActivity( AbstractIssueEditActivity activity );

    HasText name();
    HasValidable nameValidator();
    HasText description();
    HasValue<En_CaseState> state();
    HasValue<En_ImportanceLevel> importance();
    HasValue<EntityOption> company();
    HasValue<Person> initiator();
    HasValue<Person> manager();
    HasValue<DevUnit> product();
    HasValue<Boolean> isLocal();

}
