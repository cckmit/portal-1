package ru.protei.portal.ui.sitefolder.client.activity.app.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.struct.PathInfo;
import ru.protei.portal.core.model.struct.PathItem;
import ru.protei.portal.core.model.view.EntityOption;
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

    @Event
    public void onShow(SiteFolderAppEvents.Edit event) {

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        fireEvent(new ActionBarEvents.Clear());
        if (event.appId == null) {
            fireEvent(new AppEvents.InitPanelName(lang.siteFolderAppNew()));
            Application application = new Application();
            if (event.serverId != null) {
                Server server = new Server();
                server.setName(null);
                server.setId(event.serverId);
                application.setServer(server);
            }
            fillView(application);
            return;
        }
        fireEvent(new AppEvents.InitPanelName(lang.siteFolderAppEdit()));

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
    public void onSaveClicked() {

        if (!isValid()) {
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
                fireEvent(new Back());
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void fillView(Application application) {
        this.application = application;
        view.setPlatformId(application.getServer() == null ? null : application.getServer().getPlatformId());
        view.name().setValue(application.getName());
        view.server().setValue(application.getServer() == null ? null : new EntityOption(application.getServer().getName(), application.getServer().getId()));
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
    }

    private boolean isValid() {
        return view.nameValidator().isValid() && view.serverValidator().isValid();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractApplicationEditView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;

    private Application application;
    private AppEvents.InitDetails initDetails;
}
