package ru.protei.portal.ui.sitefolder.client.activity.server.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.EditHandler;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.sitefolder.client.widget.selector.servergroup.ServerGroupModel;

public interface AbstractServerEditView extends IsWidget {

    void setActivity(AbstractServerEditActivity activity);

    void setServerGroupModel(ServerGroupModel serverGroupModel);

    void setCompanyId(Long companyId);

    HasValue<String> name();

    HasValue<PlatformOption> platform();

    HasValue<ServerGroup> serverGroup();

    HasValue<String> ip();

    HasValue<String> parameters();

    HasValue<String> comment();

    HasWidgets listContainer();

    HasVisibility listContainerVisibility();

    HasVisibility listContainerHeaderVisibility();

    HasEnabled platformEnabled();

    HasEnabled serverGroupEnabled();

    HasValidable nameValidator();

    HasValidable platformValidator();

    HasVisibility createButtonVisibility();

    HasVisibility openButtonVisibility();
}
