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
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.item.AbstractSiteFolderServerListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.item.AbstractSiteFolderServerListItemView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SiteFolderServerListActivity implements Activity, AbstractSiteFolderServerListActivity, AbstractSiteFolderServerListItemActivity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderEvents.Server.ShowList event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        platformId = event.platformId;

        requestServers();
    }

    @Override
    public void onEditClicked(AbstractSiteFolderServerListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return;
        }

        Server value = itemViewToModel.get(itemView);

        if (value == null) {
            return;
        }

        fireEvent(new SiteFolderEvents.Server.Edit(value.getId()));
    }

    @Override
    public void onCreateClicked() {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        fireEvent(new SiteFolderEvents.Server.Edit());
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

    private AbstractSiteFolderServerListItemView makeItemView(Server server) {
        AbstractSiteFolderServerListItemView itemView = itemFactory.get();
        itemView.setActivity(this);
        itemView.setName(server.getName());
        itemView.setIp(server.getIp());
        itemView.setComment(server.getComment());
        itemView.setEditVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT));
        return itemView;
    }

    @Inject
    AbstractSiteFolderServerListView view;
    @Inject
    Provider<AbstractSiteFolderServerListItemView> itemFactory;
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
            AbstractSiteFolderServerListItemView itemView = makeItemView(server);
            itemViewToModel.put(itemView, server);
            view.getChildContainer().add(itemView.asWidget());
        }
    };
    private Long platformId = null;
    private PeriodicTaskService.PeriodicTaskHandler fillViewHandler;
    private Map<AbstractSiteFolderServerListItemView, Server> itemViewToModel = new HashMap<>();
}
