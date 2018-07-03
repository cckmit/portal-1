package ru.protei.portal.ui.common.client.widget.selector.district;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель статусов
 */
public abstract class DistrictModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onStateListChanged(IssueEvents.ChangeStateModel event) {
        refreshOptions();
    }

    public void subscribe( ModelSelector<DistrictInfo> selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< DistrictInfo > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        regionService.getDistrictList(
            new RequestCallback<List<DistrictInfo>>() {
                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onSuccess(List<DistrictInfo> caseStates) {
                    list.clear();
                    list.addAll( caseStates );

                    notifySubscribers();
                }
            }
        );
    }

    @Inject
    RegionControllerAsync regionService;

    private List<DistrictInfo> list = new ArrayList<>();

    List<ModelSelector<DistrictInfo>> subscribers = new ArrayList<>();
}
