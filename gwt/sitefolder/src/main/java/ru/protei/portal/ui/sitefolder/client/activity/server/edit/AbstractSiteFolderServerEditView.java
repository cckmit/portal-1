package ru.protei.portal.ui.sitefolder.client.activity.server.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractSiteFolderServerEditView extends IsWidget {

    void setActivity(AbstractSiteFolderServerEditActivity activity);

    void setCompanyId(Long companyId);

    HasValue<String> name();

    HasValue<EntityOption> platform();

    HasValue<String> ip();

    HasValue<String> parameters();

    HasValue<String> comment();

    HasEnabled platformEnabled();

    HasValidable nameValidator();

    HasValidable platformValidator();

    HasVisibility openButtonVisibility();
}
