package ru.protei.portal.ui.sitefolder.client.activity.app.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.struct.PathInfo;
import ru.protei.portal.core.model.struct.PathItem;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public abstract class ApplicationEditActivity implements Activity, AbstractApplicationEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(SiteFolderAppEvents.Edit event) {
        if (!hasPrivileges(event.appId)) {
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

        fireEvent(new ActionBarEvents.Clear());
        if (event.appId == null) {
            Application application = new Application();
            if (event.server != null) {
                application.setServer(event.server);
            }
            fillView(application);
            return;
        }

        siteFolderController.getApplication(event.appId, new RequestCallback<Application>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetObject(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Application result) {
                fillView(result);
            }
        });
    }

    @Override
    public void onComponentSelected() {
        ProductShortView value = view.component().getValue();
        view.name().setValue(value == null ? "" : value.getName());
    }

    @Override
    public void onSaveClicked() {

        if (!isValid()) {
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        fillApplication(application);

        siteFolderController.saveApplication(application, new RequestCallback<Application>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Application result) {
                fireEvent(new SiteFolderAppEvents.ChangeModel());
                fireEvent(new SiteFolderAppEvents.Changed(result));
                fireBackEvent.run();
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireBackEvent.run();
    }

    private void fillView(Application application) {
        this.application = application;
        view.setPlatformId(application.getServer() == null ? null : application.getServer().getPlatformId());
        view.component().setValue(application.getComponent() == null ? null : application.getComponent().toProductShortView());
        view.name().setValue(application.getName());
        view.server().setValue(application.getServer() == null ? null : application.getServer().toEntityOption());
        view.comment().setValue(application.getComment());
        List<PathItem> paths = application.getPaths() == null ? null : application.getPaths().getPaths();
        if (paths == null) {
            paths = new ArrayList<>();
            application.setPaths(new PathInfo(paths));
        }
        fireEvent(new PathInfoEvents.ShowList(view.pathsContainer(), paths));
    }

    private void fillApplication(Application application) {
        application.setName(view.name().getValue());
        application.setServerId(view.server().getValue().getId());
        application.setComment(view.comment().getValue());
        if (view.component().getValue() != null) {
            application.setComponentId(view.component().getValue().getId());
        }
    }

    private boolean isValid() {
        return view.nameValidator().isValid() && view.serverValidator().isValid();
    }

    private boolean hasPrivileges(Long appId) {
        if (appId == null && policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return true;
        }

        if (appId != null && policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return true;
        }

        return false;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractApplicationEditView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    PolicyService policyService;

    private Application application;
    private AppEvents.InitDetails initDetails;
    private Runnable fireBackEvent = () -> fireEvent(new Back());
}
