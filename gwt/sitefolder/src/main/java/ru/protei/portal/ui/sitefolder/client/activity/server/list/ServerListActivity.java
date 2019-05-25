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
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.HashMap;
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
    public void onCopyClicked(AbstractServerListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        Server value = itemViewToModel.get(itemView);

        if (value == null) {
            return;
        }

        fireEvent(SiteFolderServerEvents.Edit.withClone(value.getId()));
    }

    @Override
    public void onRemoveClicked(AbstractServerListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE)) {
            return;
        }

        itemViewForRemove = itemView;

        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.siteFolderServerConfirmRemove()));
    }

    @Event
    public void onServerConfirmRemove(ConfirmDialogEvents.Confirm event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        if (itemViewForRemove == null) {
            return;
        }

        Server value = itemViewToModel.get(itemViewForRemove);

        if (value == null) {
            return;
        }

        siteFolderController.removeServer(value.getId(), new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderServerNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    fireEvent(new SiteFolderServerEvents.ChangeModel());
                    fireEvent(new NotifyEvents.Show(lang.siteFolderServerRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    onRemoved(itemViewForRemove);
                } else {
                    fireEvent(new NotifyEvents.Show(lang.siteFolderServerNotRemoved(), NotifyEvents.NotifyType.ERROR));
                }
                itemViewForRemove = null;
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
        siteFolderController.getServers(query, new RequestCallback<SearchResult<Server>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(SearchResult<Server> result) {
                fillViewHandler = taskService.startPeriodicTask(result.getResults(), fillViewer, 50, 50);
            }
        });
    }

    private AbstractServerListItemView makeItemView(Server server) {
        AbstractServerListItemView itemView = itemFactory.get();
        itemView.setActivity(this);
        itemView.setName(server.getName());
        itemView.setIp(server.getIp());
        itemView.setComment(server.getComment());
        itemView.setParams(server.getParams());
        itemView.setEditVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT));
        itemView.setCopyVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE));
        itemView.setRemoveVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE));
        return itemView;
    }

    private void onRemoved(AbstractServerListItemView itemViewForRemove) {

        if (itemViewForRemove == null) {
            return;
        }

        view.getChildContainer().remove(itemViewForRemove.asWidget());
        itemViewToModel.remove(itemViewForRemove);
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
    private AbstractServerListItemView itemViewForRemove = null;
    private PeriodicTaskService.PeriodicTaskHandler fillViewHandler;
    private Map<AbstractServerListItemView, Server> itemViewToModel = new HashMap<>();
}
