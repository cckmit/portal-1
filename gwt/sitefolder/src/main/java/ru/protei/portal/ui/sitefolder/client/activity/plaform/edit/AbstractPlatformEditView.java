package ru.protei.portal.ui.sitefolder.client.activity.plaform.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractPlatformEditView extends IsWidget {

    void setActivity(AbstractPlatformEditActivity activity);

    HasValue<String> name();

    HasValue<EntityOption> company();

    HasValue<PersonShortView> manager();

    HasValue<String> parameters();

    HasValue<String> comment();

    HasWidgets listContainer();

    HasVisibility listContainerVisibility();

    HasVisibility listContainerHeaderVisibility();

    HasEnabled companyEnabled();

    HasValidable nameValidator();

    HasValidable companyValidator();

    HasVisibility openButtonVisibility();

    HasVisibility createButtonVisibility();

    HasWidgets contactsContainer();
}
