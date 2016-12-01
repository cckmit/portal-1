package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
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
    HasValue<PersonShortView> initiator();
    HasValue<PersonShortView> manager();
    HasValue<ProductShortView> product();
    HasValue<Boolean> isLocal();

    HasValidable nameValidator();
    HasValidable stateValidator();
    HasValidable importanceValidator();
    HasValidable companyValidator();
    HasValidable productValidator();
    HasValidable managerValidator();

    HasEnabled initiatorState();

    void changeCompany(Company company);

    HasWidgets getCommentsContainer();
}
