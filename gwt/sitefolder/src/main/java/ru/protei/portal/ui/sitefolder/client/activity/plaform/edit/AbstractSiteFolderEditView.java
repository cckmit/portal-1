package ru.protei.portal.ui.sitefolder.client.activity.plaform.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractSiteFolderEditView extends IsWidget {

    void setActivity(AbstractSiteFolderEditActivity activity);

    HasValue<String> name();

    HasValue<EntityOption> company();

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
}
