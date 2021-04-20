package ru.protei.portal.ui.sitefolder.client.activity.server.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.sitefolder.client.events.ServerGroupEvents;
import ru.protei.portal.ui.sitefolder.client.widget.selector.servergroup.ServerGroupModel;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.util.CrmConstants.Platform.PARAMETERS_MAX_LENGTH;

public abstract class ServerEditActivity implements Activity, AbstractServerEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setServerGroupModel(serverGroupModel);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(SiteFolderServerEvents.Edit event) {
        if (!hasPrivileges(event.serverId)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());
        this.fireBackEvent =
                event.backEvent == null ?
                () -> fireEvent(new Back()) :
                event.backEvent;

        serverIdOfAppsToBeCloned = null;

        fireEvent(new ActionBarEvents.Clear());

        if (event.serverId == null) {
            serverGroupModel.setPlatformId(event.platform == null ? null : event.platform.getId());

            if (event.serverIdToBeCloned != null) {
                requestServer(event.serverIdToBeCloned, server -> {
                    serverIdOfAppsToBeCloned = server.getId();
                    server.setId(null);
                    serverGroupModel.setPlatformId(server.getPlatformId());
                    fillView(server);
                });
                return;
            }

            Server server = new Server();
            if (event.platform != null) {
                server.setPlatform(event.platform);
            }
            fillView(server);
            return;
        }

        requestServer(event.serverId, server -> {
            serverGroupModel.setPlatformId(server.getPlatformId());
            fillView(server);
        });
    }

    @Override
    public void onSaveClicked() {

        String validationErrorMsg = validate();
        if (validationErrorMsg != null) {
            fireEvent(new NotifyEvents.Show(validationErrorMsg, NotifyEvents.NotifyType.ERROR));
            return;
        }

        fillServer(server);

        siteFolderController.saveServer(server, serverIdOfAppsToBeCloned, new FluentCallback<Server>()
                .withErrorMessage(lang.siteFolderPlatformNotSaved())
                .withSuccess(result -> {
                    serverIdOfAppsToBeCloned = null;
                    fireEvent(new SiteFolderServerEvents.ChangeModel());
                    fireEvent(new SiteFolderServerEvents.Changed(result));
                    fireBackEvent.run();
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireBackEvent.run();
    }

    @Override
    public void onOpenClicked() {
        if (server != null) {
            fireEvent(new SiteFolderAppEvents.Show(makeEntityOption(server), false));
        }
    }

    @Override
    public void onCreateClicked() {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        if (server == null) {
            return;
        }

        fireEvent(SiteFolderAppEvents.Edit.withServer(server));
    }

    @Override
    public void onCreateServerGroupClicked() {
        PlatformOption platform = view.platform().getValue();

        if (platform == null) {
            return;
        }

        fireEvent(new ServerGroupEvents.Edit(
                new ServerGroup(platform.getId()),
                serverGroup -> onServerGroupSaved(view, serverGroup, serverGroupModel)
        ));
    }

    @Override
    public void onEditServerGroupClicked(ServerGroup serverGroup) {
        ServerGroup value = view.serverGroup().getValue();

        fireEvent(new ServerGroupEvents.Edit(
                serverGroup,
                savedServerGroup -> onServerGroupSaved(view, savedServerGroup, serverGroupModel),
                removeServerGroupId -> onServerGroupRemoved(view, removeServerGroupId, serverGroupModel))
        );
    }

    @Override
    public void onPlatformChanged() {
        view.serverGroup().setValue(null);

        Long platformId = view.platform().getValue() == null ? null : view.platform().getValue().getId();

        serverGroupModel.setPlatformId(platformId);

        view.serverGroupEnabled().setEnabled(platformId != null);
    }

    private EntityOption makeEntityOption(Server server) {
        return server == null ? null : new EntityOption(server.getName(), server.getId());
    }

    private void requestServer(Long serverId, Consumer<Server> successConsumer) {
        siteFolderController.getServer(serverId, new FluentCallback<Server>()
                .withErrorMessage(lang.errGetObject())
                .withSuccess(successConsumer)
        );
    }

    private void fillView(Server server) {
        this.server = server;
        boolean isNew = isNew(server);
        boolean isCreatePrivilegeGranted = policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE);
        view.setCompanyId(server.getPlatform() == null ? null : server.getPlatform().getCompanyId());
        view.name().setValue(server.getName());
        view.platform().setValue(server.getPlatform() == null ? null : server.getPlatform().toPlatformOption());
        view.ip().setValue(server.getIp());
        view.parameters().setValue(server.getParams());
        view.comment().setValue(server.getComment());
        view.createButtonVisibility().setVisible(isCreatePrivilegeGranted);
        view.openButtonVisibility().setVisible(!isNew);
        view.listContainerVisibility().setVisible(!isNew);
        view.listContainerHeaderVisibility().setVisible(!isNew);
        view.serverGroup().setValue(createServerGroup(server));
        view.serverGroupEnabled().setEnabled(server.getPlatformId() != null);
        if (!isNew) {
            fireEvent(new SiteFolderAppEvents.ShowList(view.listContainer(), server.getId()));
        }
    }

    private void fillServer(Server server) {
        server.setName(view.name().getValue());
        server.setPlatformId(view.platform().getValue().getId());
        server.setIp(view.ip().getValue());
        server.setParams(view.parameters().getValue());
        server.setComment(view.comment().getValue());

        ServerGroup serverGroup = view.serverGroup().getValue();

        server.setServerGroupId(serverGroup == null ? null : serverGroup.getId());
        server.setServerGroupName(serverGroup == null ? null : serverGroup.getName());
    }

    private boolean isValid() {
        return view.nameValidator().isValid() && view.platformValidator().isValid();
    }

    private String validate() {
        boolean isValid = view.nameValidator().isValid() && view.platformValidator().isValid();

        if (!isValid) {
            return lang.errFieldsRequired();
        }

        if (view.parameters().getValue().length() > PARAMETERS_MAX_LENGTH) {
            return lang.errAccessParametersLengthExceeded(PARAMETERS_MAX_LENGTH);
        }

        return null;
    }

    private boolean hasPrivileges(Long serverId) {
        if (serverId == null && policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return true;
        }

        if (serverId != null && policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return true;
        }

        return false;
    }

    private boolean isNew(Server server) {
        return server.getId() == null;
    }

    private ServerGroup createServerGroup(Server server) {
        if (server.getServerGroupId() == null) {
            return null;
        }

        ServerGroup serverGroup = new ServerGroup();
        serverGroup.setName(server.getServerGroupName());
        serverGroup.setPlatformId(server.getPlatformId());
        serverGroup.setId(server.getServerGroupId());

        return serverGroup;
    }

    private void onServerGroupSaved(AbstractServerEditView view,
                                    ServerGroup serverGroup,
                                    ServerGroupModel serverGroupModel) {

        ServerGroup currentServerGroup = view.serverGroup().getValue();
        if (serverGroup.equals(currentServerGroup)) {
            view.serverGroup().setValue(serverGroup);
        }

        serverGroupModel.clearCache();
    }

    private void onServerGroupRemoved(AbstractServerEditView view,
                                      Long serverGroupId,
                                      ServerGroupModel serverGroupModel) {

        Long currentServerGroupId = view.serverGroup().getValue() == null ? null : view.serverGroup().getValue().getId();

        if (serverGroupId.equals(currentServerGroupId)) {
            view.serverGroup().setValue(null);
        }

        serverGroupModel.clearCache();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractServerEditView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    PolicyService policyService;
    @Inject
    ServerGroupModel serverGroupModel;

    private Server server;
    private Long serverIdOfAppsToBeCloned;
    private AppEvents.InitDetails initDetails;
    private Runnable fireBackEvent = () -> fireEvent(new Back());
}
