package ru.protei.portal.ui.common.client.widget.selector.project;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;

public abstract class ProjectModel implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        refreshOptions();
    }

    public void subscribe(ModelSelector<ProjectInfo> documentTypeSelector) {
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
        ProjectQuery query = new ProjectQuery();
        query.setStates(new HashSet<>(Arrays.asList(En_RegionState.values())));
        regionService.getProjectsByRegions(query, new RequestCallback<Map<String, List<ProjectInfo>>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Map<String, List<ProjectInfo>> result) {
                list.clear();
                result.forEach((a, b) -> list.addAll(b));
                notifySubscribers();
            }
        });
    }

    @Inject
    RegionServiceAsync regionService;


    @Inject
    Lang lang;

    private List<ProjectInfo> list = new LinkedList<>();

    List<ModelSelector<ProjectInfo>> subscribers = new LinkedList<>();
}
