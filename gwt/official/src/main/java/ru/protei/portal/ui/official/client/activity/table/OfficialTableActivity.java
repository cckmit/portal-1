package ru.protei.portal.ui.official.client.activity.table;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
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
        CREATE_ACTION = lang.buttonCreate();

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
    }

    @Event
    public void onShow(OfficialMemberEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.OFFICIAL_EDIT ) ?
                new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.OFFICIAL ) :
                new ActionBarEvents.Clear()
        );
        requestTotalCount();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.init = initDetails;
    }

    private void requestTotalCount() {
        view.clearRecords();
        officialService.getOfficialsByRegions(getQuery(), new RequestCallback<Map<String, List<Official>>>() {
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

    private OfficialQuery getQuery() {
        OfficialQuery query = new OfficialQuery();
        query.setSearchString(filterView.searchPattern().getValue());
        query.setFrom(filterView.dateRange().getValue().from);
        query.setTo(filterView.dateRange().getValue().to);
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
        query.setProductId(filterView.product().getValue() == null
                ? null
                : filterView.product().getValue().getId());
        query.setRegionId(filterView.region().getValue() == null
                ? null
                :filterView.region().getValue().getId());

        return query;
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

    private static String CREATE_ACTION;

    @Inject
    TableAnimation animation;
    @Inject
    OfficialServiceAsync officialService;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails init;

    @Inject
    private AbstractOfficialTableView view;

    @Inject
    private AbstractOfficialFilterView filterView;
}
