package ru.protei.portal.ui.sitefolder.client.activity.plaform.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

public abstract class SiteFolderEditActivity implements Activity, AbstractSiteFolderEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(SiteFolderEvents.Platform.Edit event) {

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        fireEvent(new ActionBarEvents.Clear());
        if (event.platformId == null) {
            fireEvent(new AppEvents.InitPanelName(lang.siteFolderPlatformNew()));
            fillView(new Platform());
            return;
        }
        fireEvent(new AppEvents.InitPanelName(lang.siteFolderPlatformEdit()));

        siteFolderController.getPlatform(event.platformId, new RequestCallback<Platform>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetObject(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Platform result) {
                fillView(result);
            }
        });
    }

    @Override
    public void onSaveClicked() {

        if (!isValid()) {
            return;
        }

        fillPlatform(platform);

        siteFolderController.savePlatform(platform, new RequestCallback<Platform>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Platform result) {
                fireEvent(new SiteFolderEvents.Platform.ChangeModel());
                fireEvent(new SiteFolderEvents.Platform.Changed(result));
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
        if (platform != null) {
            fireEvent(new SiteFolderEvents.Server.Show(platform.getId()));
        }
    }

    private void fillView(Platform platform) {
        this.platform = platform;
        view.name().setValue(platform.getName());
        view.company().setValue(EntityOption.fromCompany(platform.getCompany()));
        view.parameters().setValue(platform.getParams());
        view.comment().setValue(platform.getComment());
        view.openButtonVisibility().setVisible(platform.getId() != null);
    }

    private void fillPlatform(Platform platform) {
        platform.setName(view.name().getValue());
        platform.setCompanyId(view.company().getValue().getId());
        platform.setParams(view.parameters().getValue());
        platform.setComment(view.comment().getValue());
    }

    private boolean isValid() {
        return view.nameValidator().isValid() && view.companyValidator().isValid();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractSiteFolderEditView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;

    private Platform platform;
    private AppEvents.InitDetails initDetails;
}
