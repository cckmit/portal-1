package ru.protei.portal.ui.common.client.widget.selector.project;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;

public abstract class ProjectModel implements Activity {


    @Event
    public void onInit(AuthEvents.Success event) {
        this.profile = event.profile;
        refreshOptions();
    }

    public void subscribe( SelectorWithModel<ProjectInfo> documentTypeSelector) {
        subscribers.add(documentTypeSelector);
        documentTypeSelector.fillOptions(list);
    }

    private void notifySubscribers() {
        subscribers.forEach(selector -> {
            selector.fillOptions(list);
            selector.refreshValue();
        });
    }

    private void refreshOptions() {
        regionService.getProjectsList(new RequestCallback<List<ProjectInfo>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<ProjectInfo> result) {
                list.clear();
                list.addAll(result);

                notifySubscribers();
            }
        });
    }

    @Inject
    RegionControllerAsync regionService;


    @Inject
    Lang lang;

    private Profile profile;
    private List<ProjectInfo> list = new LinkedList<>();

    List<SelectorWithModel<ProjectInfo>> subscribers = new LinkedList<>();
}
