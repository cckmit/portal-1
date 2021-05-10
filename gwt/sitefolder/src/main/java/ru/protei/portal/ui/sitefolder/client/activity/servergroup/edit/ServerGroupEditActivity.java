package ru.protei.portal.ui.sitefolder.client.activity.servergroup.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.view.dialogdetails.DialogDetailsView;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.sitefolder.client.events.ServerGroupEvents;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public abstract class ServerGroupEditActivity implements
        Activity, AbstractServerGroupEditActivity, AbstractDialogDetailsActivity {

    @Inject
    public void onInit() {
        dialogDetailsView.setActivity(this);
    }

    @Event
    public void onShow(ServerGroupEvents.Edit event) {
        this.serverGroup = event.serverGroup;
        this.onSave = event.onSave;
        this.onRemove = event.onRemove;

        fillView(event.serverGroup);

        dialogDetailsView.getBodyContainer().clear();
        dialogDetailsView.getBodyContainer().add(view.asWidget());

        dialogDetailsView.showPopup();
    }

    @Override
    public void onSaveClicked() {
        if (isBlank(view.name().getValue())) {
            fireEvent(new NotifyEvents.Show(lang.siteFolderServerGroupNameIsMissing(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        serverGroup.setName(view.name().getValue());

        serverGroupService.saveServerGroup(serverGroup, new FluentCallback<ServerGroup>()
                .withSuccess(serverGroup -> {
                    dialogDetailsView.hidePopup();

                    fireEvent(new NotifyEvents.Show(lang.siteFolderServerGroupSaved(), NotifyEvents.NotifyType.SUCCESS));

                    onSave.accept(serverGroup);
                })
        );
    }

    @Override
    public void onCancelClicked() {
        dialogDetailsView.hidePopup();
    }

    @Override
    public void onRemoveClicked() {
        if (serverGroup.getId() == null) {
            return;
        }

        serverGroupService.removeServerGroup(serverGroup.getId(), new FluentCallback<Long>()
                .withSuccess(serverGroupId -> {
                    dialogDetailsView.hidePopup();

                    fireEvent(new NotifyEvents.Show(lang.siteFolderServerGroupRemoved(), NotifyEvents.NotifyType.SUCCESS));

                    onRemove.accept(serverGroupId);
                })
        );
    }

    private void fillView(ServerGroup serverGroup) {
        boolean isNew = serverGroup.getId() == null;

        view.name().setValue(serverGroup.getName());

        dialogDetailsView.setHeader(isNew ? lang.siteFolderServerGroupCreate() : lang.siteFolderServerGroupEdit());
        dialogDetailsView.removeButtonVisibility().setVisible(!isNew);
    }

    @Inject
    Lang lang;

    @Inject
    AbstractServerGroupEditView view;

    @Inject
    DialogDetailsView dialogDetailsView;

    @Inject
    SiteFolderControllerAsync serverGroupService;

    private ServerGroup serverGroup;

    private Consumer<ServerGroup> onSave;
    private Consumer<Long> onRemove;
}
