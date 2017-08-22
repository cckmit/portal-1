package ru.protei.portal.ui.official.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.OfficialEvents;
import ru.protei.portal.ui.common.client.service.OfficialServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Активность таблицы должностных лиц
 */
public abstract class OfficialTableActivity implements AbstractOfficialsTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Override
    public void onEditClicked(Official value) {

    }

    @Event
    public void onShow(OfficialEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());
        requestTotalCount();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.init = initDetails;
    }

    private void requestTotalCount() {
        view.clearRecords();
        view.setRecordCount(2l);
    }

    @Override
    public void loadData(int offset, int limit, final AsyncCallback<List<Official>> asyncCallback) {
        officialService.getOfficialList(new RequestCallback<List<Official>>() {
            @Override
            public void onError(Throwable throwable) {
                asyncCallback.onFailure(throwable);
            }

            @Override
            public void onSuccess(List<Official> officials) {
                asyncCallback.onSuccess(officials);
            }
        });

    }

    private AppEvents.InitDetails init;
    @Inject
    private AbstractOfficialTableView view;

    @Inject
    OfficialServiceAsync officialService;

    @Override
    public void onAttachClicked(Official value, IsWidget widget) {

    }
}
