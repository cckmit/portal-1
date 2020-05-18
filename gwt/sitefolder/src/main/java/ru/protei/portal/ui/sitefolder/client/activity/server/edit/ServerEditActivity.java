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
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class ServerEditActivity implements Activity, AbstractServerEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(SiteFolderServerEvents.Edit event) {
        if (!hasPrivileges(event.serverId)) {
            fireEvent(new ForbiddenEvents.Show());
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

            if (event.serverIdToBeCloned != null) {
                requestServer(event.serverIdToBeCloned, server -> {
                    serverIdOfAppsToBeCloned = server.getId();
                    server.setId(null);
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

        requestServer(event.serverId, this::fillView);
    }

    @Override
    public void onSaveClicked() {

        if (!isValid()) {
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
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
            fireEvent(new SiteFolderAppEvents.Show(server.getId(), false));
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
    }

    private boolean isValid() {
        return view.nameValidator().isValid() && view.platformValidator().isValid();
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

    @Inject
    Lang lang;
    @Inject
    AbstractServerEditView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    PolicyService policyService;

    private Server server;
    private Long serverIdOfAppsToBeCloned;
    private AppEvents.InitDetails initDetails;
    private Runnable fireBackEvent;
}
