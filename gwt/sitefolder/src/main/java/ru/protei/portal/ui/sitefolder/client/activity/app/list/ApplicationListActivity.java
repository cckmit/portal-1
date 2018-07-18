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
import ru.protei.portal.ui.common.client.events.SiteFolderEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.item.AbstractApplicationListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.item.AbstractApplicationListItemView;

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
    public void onShow(SiteFolderEvents.App.ShowList event) {
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

        fireEvent(new SiteFolderEvents.App.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(AbstractApplicationListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE)) {
            return;
        }

        Application value = itemViewToModel.get(itemView);

        if (value == null) {
            return;
        }

        appIdForRemove = value.getId();
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.siteFolderAppConfirmRemove()));
    }

    @Event
    public void onServerConfirmRemove(ConfirmDialogEvents.Confirm event) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        if (appIdForRemove == null) {
            return;
        }

        siteFolderController.removeApplication(appIdForRemove, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderAppNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                appIdForRemove = null;
                if (result) {
                    fireEvent(new SiteFolderEvents.App.ChangeModel());
                    fireEvent(new NotifyEvents.Show(lang.siteFolderAppRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    requestApps();
                } else {
                    fireEvent(new NotifyEvents.Show(lang.siteFolderAppNotRemoved(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
    }

    private void requestApps() {

        if (fillViewHandler != null) {
            fillViewHandler.cancel();
        }
        view.getChildContainer().clear();
        itemViewToModel.clear();

        ApplicationQuery query = new ApplicationQuery();
        query.setServerId(serverId);
        siteFolderController.getApplications(query, new RequestCallback<List<Application>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Application> result) {
                fillViewHandler = taskService.startPeriodicTask(result, fillViewer, 50, 50);
            }
        });
    }

    private AbstractApplicationListItemView makeItemView(Application application) {
        AbstractApplicationListItemView itemView = itemFactory.get();
        itemView.setActivity(this);
        itemView.setName(application.getName());
        itemView.setComment(application.getComment());
        itemView.setEditVisible(policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT));
        return itemView;
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
    private Long appIdForRemove = null;
    private PeriodicTaskService.PeriodicTaskHandler fillViewHandler;
    private Map<AbstractApplicationListItemView, Application> itemViewToModel = new HashMap<>();
}

