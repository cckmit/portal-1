package ru.protei.portal.ui.sitefolder.client.activity.server.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderServerEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.item.AbstractServerListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.item.AbstractServerListItemView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class ServerListActivity implements Activity, AbstractServerListActivity, AbstractServerListItemActivity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderServerEvents.ShowList event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        platformId = event.platformId;

        requestServers();
    }

    @Event
    public void onServerChanged(SiteFolderServerEvents.Changed event) {
        onChanged(event.server);
    }

    @Override
    public void onEditClicked(AbstractServerListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return;
        }

        Server value = itemViewToModel.get(itemView);

        if (value == null) {
            return;
        }

        fireEvent(new SiteFolderServerEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(AbstractServerListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE)) {
            return;
        }

        Server value = itemViewToModel.get(itemView);

        if (value == null) {
            return;
        }

        serverIdForRemove = value.getId();
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.siteFolderServerConfirmRemove()));
    }

    @Event
    public void onServerConfirmRemove(ConfirmDialogEvents.Confirm event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        if (serverIdForRemove == null) {
            return;
        }

        siteFolderController.removeServer(serverIdForRemove, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderServerNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    fireEvent(new SiteFolderServerEvents.ChangeModel());
                    fireEvent(new NotifyEvents.Show(lang.siteFolderServerRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    onRemoved(serverIdForRemove);
                } else {
                    fireEvent(new NotifyEvents.Show(lang.siteFolderServerNotRemoved(), NotifyEvents.NotifyType.ERROR));
                }
                serverIdForRemove = null;
            }
        });
    }

    private void requestServers() {

        if (fillViewHandler != null) {
            fillViewHandler.cancel();
        }
        view.getChildContainer().clear();
        itemViewToModel.clear();

        ServerQuery query = new ServerQuery();
        query.setPlatformId(platformId);
        siteFolderController.getServers(query, new RequestCallback<List<Server>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Server> result) {
                fillViewHandler = taskService.startPeriodicTask(result, fillViewer, 50, 50);
            }
        });
    }

    private AbstractServerListItemView makeItemView(Server server) {
        AbstractServerListItemView itemView = itemFactory.get();
        itemView.setActivity(this);
        itemView.setName(server.getName());
        itemView.setIp(server.getIp());
        itemView.setComment(server.getComment());
        itemView.setEditVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT));
        return itemView;
    }

    private void onRemoved(Long id) {

        if (id == null) {
            return;
        }

        for (Map.Entry<AbstractServerListItemView, Server> entry : itemViewToModel.entrySet()) {
            AbstractServerListItemView itemView = entry.getKey();
            Server server = entry.getValue();
            if (id.equals(server.getId())) {
                view.getChildContainer().remove(itemView.asWidget());
                itemViewToModel.remove(itemView);
                break;
            }
        }
    }

    private void onChanged(Server server) {

        if (server == null || server.getId() == null) {
            return;
        }

        for (Map.Entry<AbstractServerListItemView, Server> entry : itemViewToModel.entrySet()) {
            AbstractServerListItemView iw = entry.getKey();
            Server s = entry.getValue();
            if (server.getId().equals(s.getId())) {
                AbstractServerListItemView itemView = makeItemView(server);
                view.getChildContainer().remove(iw.asWidget());
                view.getChildContainer().add(itemView.asWidget());
                itemViewToModel.replace(iw, s, server);
                break;
            }
        }
    }

    @Inject
    AbstractServerListView view;
    @Inject
    Provider<AbstractServerListItemView> itemFactory;
    @Inject
    PeriodicTaskService taskService;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private Consumer<Server> fillViewer = new Consumer<Server>() {
        @Override
        public void accept(Server server) {
            AbstractServerListItemView itemView = makeItemView(server);
            itemViewToModel.put(itemView, server);
            view.getChildContainer().add(itemView.asWidget());
        }
    };
    private Long platformId = null;
    private Long serverIdForRemove = null;
    private PeriodicTaskService.PeriodicTaskHandler fillViewHandler;
    private Map<AbstractServerListItemView, Server> itemViewToModel = new HashMap<>();
}
