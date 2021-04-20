package ru.protei.portal.ui.sitefolder.client.view.servergroup.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.sitefolder.client.activity.servergroup.edit.AbstractServerGroupEditView;

public class ServerGroupEditView extends Composite implements AbstractServerGroupEditView {
    public ServerGroupEditView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initDebugIds();
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    private void initDebugIds() {
        name.ensureDebugId(DebugIds.SITE_FOLDER.SERVER_GROUP.NAME);
    }

    @UiField
    TextBox name;

    interface ServerGroupEditViewUiBinder extends UiBinder<HTMLPanel, ServerGroupEditView> {}
    private static ServerGroupEditViewUiBinder ourUiBinder = GWT.create(ServerGroupEditViewUiBinder.class);
}
