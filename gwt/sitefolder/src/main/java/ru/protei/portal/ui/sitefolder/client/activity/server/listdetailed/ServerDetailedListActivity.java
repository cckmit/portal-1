package ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderServerEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed.item.AbstractServerDetailedListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed.item.AbstractServerDetailedListItemView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public abstract class ServerDetailedListActivity implements Activity, AbstractServerDetailedListActivity, AbstractServerDetailedListItemActivity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderServerEvents.ShowDetailedList event) {
        if (event.platformId == null) {
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());

        if (fillViewHandler != null) {
            fillViewHandler.cancel();
        }
        view.getChildContainer().clear();
        itemViewToModel.clear();

        requestServers(event.platformId);
    }

    @Override
    public void onEditClicked(AbstractServerDetailedListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return;
        }

        Server value = itemViewToModel.get(itemView);

        if (value == null) {
            return;
        }

        fireEvent(new SiteFolderServerEvents.Edit(value.getId()));
    }

    private void requestServers(Long platformId) {
        siteFolderController.getServersWithAppsNames(ServerQuery.forPlatformId(platformId), new RequestCallback<SearchResult<Server>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }
            @Override
            public void onSuccess(SearchResult<Server> servers) {
                if (servers == null) {
                    servers = new SearchResult<>();
                }
                fillViewHandler = taskService.startPeriodicTask(servers.getResults(), fillViewer, 50, 50);
            }
        });
    }

    private AbstractServerDetailedListItemView makeItemView(Server server) {
        AbstractServerDetailedListItemView itemView = itemFactory.get();
        itemView.setActivity(this);
        itemView.setName(server.getName() + " – " + server.getIp());
        itemView.setParameters(server.getParams());
        itemView.setApps(stream(server.getAppNames())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "))
        );
        itemView.setComment(server.getComment());
        itemView.setEditVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT));
        return itemView;
    }

    @Inject
    AbstractServerDetailedListView view;
    @Inject
    Provider<AbstractServerDetailedListItemView> itemFactory;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    PeriodicTaskService taskService;
    @Inject
    PolicyService policyService;
    @Inject
    Lang lang;

    private Consumer<Server> fillViewer = new Consumer<Server>() {
        @Override
        public void accept(Server server) {
            AbstractServerDetailedListItemView itemView = makeItemView(server);
            itemViewToModel.put(itemView, server);
            view.getChildContainer().add(itemView.asWidget());
        }
    };
    private PeriodicTaskService.PeriodicTaskHandler fillViewHandler;
    private Map<AbstractServerDetailedListItemView, Server> itemViewToModel = new HashMap<>();
}
