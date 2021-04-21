package ru.protei.portal.ui.sitefolder.client.activity.server.edit;

import ru.protei.portal.core.model.ent.ServerGroup;

public interface AbstractServerEditActivity {

    void onSaveClicked();
    void onCancelClicked();
    void onOpenClicked();
    void onCreateClicked();

    void onCreateServerGroupClicked();
    void onEditServerGroupClicked(ServerGroup serverGroup);

    void onPlatformChanged();
}
