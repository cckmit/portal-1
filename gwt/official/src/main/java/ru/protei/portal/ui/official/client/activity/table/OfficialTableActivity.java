package ru.protei.portal.ui.official.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.OfficialServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.official.client.activity.filter.AbstractOfficialFilterActivity;
import ru.protei.portal.ui.official.client.activity.filter.AbstractOfficialFilterView;

import java.util.List;
import java.util.Map;

/**
 * Активность таблицы должностных лиц
 */
public abstract class OfficialTableActivity
        implements AbstractOfficialsTableActivity, AbstractOfficialFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setAnimation(animation);
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());
    }

    @Override
    public void onEditClicked(Official value) {

    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
        officialService.initMembers(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                if (!aBoolean) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                }
                return;
            }
        });
    }

    @Event
    public void onShow(OfficialMemberEvents.Show event) {
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
        officialService.getOfficialsByRegions(new RequestCallback<Map<String, List<Official>>>() {
            @Override
            public void onSuccess(Map<String, List<Official>> result) {
                fillRows(result);
            }

            @Override
            public void onError(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }
        });


    }

    @Override
    public void onFilterChanged() {
        requestTotalCount();
    }

    private void fillRows(Map<String, List<Official>> result) {
        view.clearRecords();
        for (Map.Entry<String, List<Official>> entry: result.entrySet()) {
            view.addSeparator(entry.getKey());

            for (Official official: entry.getValue()) {
                view.addRow(official);
            }
        }
    }

    @Override
    public void onItemClicked(Official value) {
        showPreview(value);
    }

    private void showPreview(Official value) {
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new OfficialMemberEvents.ShowPreview( view.getPreviewContainer(), value.getId() ) );
        }
    }

    @Override
    public void onAttachClicked(Official value, IsWidget widget) {

    }

    @Inject
    TableAnimation animation;
    @Inject
    OfficialServiceAsync officialService;

    @Inject
    Lang lang;

    private AppEvents.InitDetails init;

    @Inject
    private AbstractOfficialTableView view;

    @Inject
    private AbstractOfficialFilterView filterView;
}
