package ru.protei.portal.ui.sitefolder.client.activity.app.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderAppEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.item.AbstractApplicationListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.item.AbstractApplicationListItemView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class ApplicationListActivity implements Activity, AbstractApplicationListActivity, AbstractApplicationListItemActivity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderAppEvents.ShowList event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        serverId = event.serverId;

        requestApps();
    }

    @Override
    public void onEditClicked(AbstractApplicationListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return;
        }

        Application value = itemViewToModel.get(itemView);

        if (value == null) {
            return;
        }

        fireEvent(new SiteFolderAppEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(AbstractApplicationListItemView itemView) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE)) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.siteFolderAppConfirmRemove(), removeAction(itemView)));
    }

    private void requestApps() {

        if (fillViewHandler != null) {
            fillViewHandler.cancel();
        }
        view.getChildContainer().clear();
        itemViewToModel.clear();

        ApplicationQuery query = new ApplicationQuery();
        query.setServerId(serverId);
        siteFolderController.getApplications(query, new RequestCallback<SearchResult<Application>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(SearchResult<Application> result) {
                fillViewHandler = taskService.startPeriodicTask(result.getResults(), fillViewer, 50, 50);
            }
        });
    }

    private AbstractApplicationListItemView makeItemView(Application application) {
        AbstractApplicationListItemView itemView = itemFactory.get();
        itemView.setActivity(this);
        itemView.setName(application.getName());
        itemView.setComment(application.getComment());
        itemView.setEditVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT));
        itemView.setRemoveVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE));
        return itemView;
    }

    private Runnable removeAction(AbstractApplicationListItemView itemView) {
        return () -> {
            Application value = itemViewToModel.get(itemView);

            if (value == null) {
                return;
            }

            siteFolderController.removeApplication(value.getId(), new RequestCallback<Long>() {
                @Override
                public void onError(Throwable throwable) {
                    fireEvent(new NotifyEvents.Show(lang.siteFolderAppNotRemoved(), NotifyEvents.NotifyType.ERROR));
                }

                @Override
                public void onSuccess(Long result) {
                    fireEvent(new SiteFolderAppEvents.ChangeModel());
                    fireEvent(new NotifyEvents.Show(lang.siteFolderAppRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    onRemoved(itemView);
                }
            });
        };
    }

    private void onRemoved(AbstractApplicationListItemView itemViewForRemove) {
        if (itemViewForRemove == null) {
            return;
        }

        view.getChildContainer().remove(itemViewForRemove.asWidget());
        itemViewToModel.remove(itemViewForRemove);
    }

    @Inject
    AbstractApplicationListView view;
    @Inject
    Provider<AbstractApplicationListItemView> itemFactory;
    @Inject
    PeriodicTaskService taskService;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private Consumer<Application> fillViewer = new Consumer<Application>() {
        @Override
        public void accept(Application application) {
            AbstractApplicationListItemView itemView = makeItemView(application);
            itemViewToModel.put(itemView, application);
            view.getChildContainer().add(itemView.asWidget());
        }
    };
    private Long serverId = null;
    private PeriodicTaskService.PeriodicTaskHandler fillViewHandler;
    private Map<AbstractApplicationListItemView, Application> itemViewToModel = new HashMap<>();
}

