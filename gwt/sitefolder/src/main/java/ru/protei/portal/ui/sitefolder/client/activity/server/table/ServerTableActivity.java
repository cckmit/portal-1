package ru.protei.portal.ui.sitefolder.client.activity.server.table;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import static ru.protei.portal.core.model.helper.CollectionUtils.nullsLast;

public abstract class ServerTableActivity implements Activity, AbstractServerTableActivity {
    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderServerEvents.ShowTable event) {
        if (event.platform == null || event.platform.getId() == null) {
            return;
        }

        view.createButtonVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE));

        this.platform = event.platform;
        backEvent = event.isPlatformPreview ?
                () -> fireEvent(new SiteFolderPlatformEvents.Show(true)) :
                () -> fireEvent(new Back());

        event.parent.clear();
        event.parent.add(view.asWidget());

        resetFilter();
        reloadTable();
    }

    @Override
    public void onCreateClicked() {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        if (platform == null) {
            return;
        }

        fireEvent(SiteFolderServerEvents.Edit.withPlatform(platform).withBackEvent(backEvent));
    }

    @Override
    public void onCopyClicked(Server value) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        if (value == null) {
            return;
        }

        fireEvent(SiteFolderServerEvents.Edit.withClone(value.getId()).withBackEvent(backEvent));
    }

    @Override
    public void onEditClicked(Server value) {
        if (value == null) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return;
        }

        fireEvent(new SiteFolderServerEvents.Edit(value.getId()).withBackEvent(backEvent));
    }

    @Override
    public void onRemoveClicked(Server value) {
        if (value == null) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE)) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.siteFolderServerConfirmRemove(), removeAction(value.getId())));
    }

    @Override
    public void onOpenAppsClicked(Server server) {
        if (server != null) {
            fireEvent(new SiteFolderAppEvents.Show(makeEntityOption(server), false));
        }
    }

    @Override
    public void onFilterChanged() {
        reloadTable();
    }

    @Override
    public ServerGroup makeGroup(Server server) {
        return makeServerGroup(server);
    }

    @Override
    public String makeGroupName(ServerGroup group) {
        return group == null ? lang.siteFolderServerGroupWithoutGroup() : group.getName();
    }

    private void resetFilter() {
        view.nameOrIp().setValue(null);
        view.sortField().setValue(En_SortField.name);
        view.sortDir().setValue(false);
    }

    private void reloadTable() {
        view.clearRecords();
        requestServers();
    }

    private void requestServers() {
        ServerQuery serverQuery = getQuery();
        siteFolderController.getServers(serverQuery, new FluentCallback<SearchResult<Server>>()
                .withSuccess(sr -> {
                    view.addRecords(nullsLast(sr.getResults(), Server::getServerGroupId));
                }));
    }

    private ServerQuery getQuery() {
        ServerQuery serverQuery = new ServerQuery();
        serverQuery.setPlatformId(platform.getId());
        serverQuery.useSort(
                view.sortField().getValue(),
                view.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC
        );
        serverQuery.setNameOrIp(view.nameOrIp().getValue());
        return serverQuery;
    }

    private EntityOption makeEntityOption(Server server) {
        return server == null ? null : new EntityOption(server.getName(), server.getId());
    }

    private Runnable removeAction(Long serverId) {
        return () -> siteFolderController.removeServer(serverId, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderServerNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderServerRemoved(), NotifyEvents.NotifyType.SUCCESS));

                platform.setServersCount(platform.getServersCount() - 1);

                fireEvent(new SiteFolderPlatformEvents.Changed(platform));
                reloadTable();
            }
        });
    }

    private ServerGroup makeServerGroup(Server server) {
        if (server.getServerGroupId() == null) {
            return null;
        }

        ServerGroup serverGroup = new ServerGroup();

        serverGroup.setId(server.getServerGroupId());
        serverGroup.setName(server.getServerGroupName());
        serverGroup.setPlatformId(server.getPlatformId());

        return serverGroup;
    }

    @Inject
    PolicyService policyService;

    @Inject
    Lang lang;

    @Inject
    SiteFolderControllerAsync siteFolderController;

    @Inject
    AbstractServerTableView view;

    private Platform platform;
    private Runnable backEvent = () -> fireEvent(new Back());
}
