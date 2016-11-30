package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.ContactShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Представление создания и редактирования обращения
 */
public interface AbstractIssueEditView extends IsWidget {

    void setActivity( AbstractIssueEditActivity activity );

    HasValue<String> name();
    HasText description();
    HasValue<En_CaseState> state();
    HasValue<En_ImportanceLevel> importance();
    HasValue<EntityOption> company();
    HasValue<ContactShortView> initiator();
    HasValue<EntityOption> manager();
    HasValue<EntityOption> product();
    HasValue<Boolean> isLocal();

    HasValidable nameValidator();
    HasValidable stateValidator();
    HasValidable importanceValidator();
    HasValidable companyValidator();
    HasValidable productValidator();
    HasValidable managerValidator();

    HasEnabled initiatorState();

    void changeCompany(Company company);

}