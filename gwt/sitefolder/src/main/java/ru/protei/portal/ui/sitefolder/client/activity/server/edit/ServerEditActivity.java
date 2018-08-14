package ru.protei.portal.ui.sitefolder.client.activity.server.edit;

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
import ru.protei.portal.ui.common.shared.model.RequestCallback;

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

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        serverIdToBeCloned = null;

        fireEvent(new ActionBarEvents.Clear());
        if (event.serverId == null) {
            fireEvent(new AppEvents.InitPanelName(lang.siteFolderServerNew()));

            if (event.serverIdToBeCloned != null) {
                requestServer(event.serverIdToBeCloned, server -> {
                    serverIdToBeCloned = server.getId();
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
        fireEvent(new AppEvents.InitPanelName(lang.siteFolderServerEdit()));

        requestServer(event.serverId, this::fillView);
    }

    @Override
    public void onSaveClicked() {

        if (!isValid()) {
            return;
        }

        fillServer(server);

        siteFolderController.saveServer(server, serverIdToBeCloned, new RequestCallback<Server>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Server result) {
                serverIdToBeCloned = null;
                fireEvent(new SiteFolderServerEvents.ChangeModel());
                fireEvent(new SiteFolderServerEvents.Changed(result));
                fireEvent(new Back());
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onOpenClicked() {
        if (server != null) {
            fireEvent(new SiteFolderAppEvents.Show(server.getId()));
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
        boolean isNotNew = server.getId() != null;
        boolean isCreatePrivilegeGranted = policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE);
        view.setCompanyId(server.getPlatform() == null ? null : server.getPlatform().getCompanyId());
        view.name().setValue(server.getName());
        view.platform().setValue(server.getPlatform() == null ? null : server.getPlatform().toEntityOption());
        view.ip().setValue(server.getIp());
        view.parameters().setValue(server.getParams());
        view.comment().setValue(server.getComment());
        view.createButtonVisibility().setVisible(isCreatePrivilegeGranted);
        view.openButtonVisibility().setVisible(isNotNew);
        view.listContainerVisibility().setVisible(isNotNew);
        view.listContainerHeaderVisibility().setVisible(isNotNew);
        if (isNotNew) {
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

    @Inject
    Lang lang;
    @Inject
    AbstractServerEditView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    PolicyService policyService;

    private Server server;
    private Long serverIdToBeCloned;
    private AppEvents.InitDetails initDetails;
}
