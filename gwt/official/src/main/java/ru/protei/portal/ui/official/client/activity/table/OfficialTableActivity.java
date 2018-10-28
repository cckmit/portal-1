package ru.protei.portal.ui.official.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.OfficialControllerAsync;
import ru.protei.portal.ui.common.client.widget.attachment.popup.AttachPopup;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.official.client.activity.filter.AbstractOfficialFilterActivity;
import ru.protei.portal.ui.official.client.activity.filter.AbstractOfficialFilterView;

import java.util.List;
import java.util.Map;

/**
 * Активность таблицы должностных лиц
 */
public abstract class OfficialTableActivity
        implements AbstractOfficialTableActivity, AbstractOfficialFilterActivity, Activity {

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
        fireEvent(new OfficialEvents.Edit(value.getId()));
    }

    @Override
    public void updateRow(Long officialId) {

    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.OFFICIAL.equals(event.identity)) {
            return;
        }

        fireEvent(new OfficialEvents.Edit(null));
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow(OfficialMemberEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.OFFICIAL_EDIT) ?
                new ActionBarEvents.Add(CREATE_ACTION, null, UiConstants.ActionBarIdentity.OFFICIAL) :
                new ActionBarEvents.Clear()
        );
        requestTotalCount();
    }


    @Event
    public void onReload(OfficialMemberEvents.ReloadPage event) {

        requestTotalCount();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
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
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
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
                : filterView.region().getValue().getId());

        return query;
    }

    @Override
    public void onFilterChanged() {
        requestTotalCount();
    }

    private void fillRows(Map<String, List<Official>> result) {
        view.clearRecords();
        for (Map.Entry<String, List<Official>> entry : result.entrySet()) {
            if (filterView.region().getValue() == null || entry.getKey()
                    .equals(filterView.region().getValue().getDisplayText())) {

                view.addSeparator(entry.getKey());
                for (Official official : entry.getValue()) {
                    view.addRow(official);
                }
            }
        }
    }

    @Override
    public void onItemClicked(Official value) {
        showPreview(value);
    }

    private void showPreview(Official value) {
        if (value == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new OfficialMemberEvents.ShowPreview(view.getPreviewContainer(), value.getId()));
        }
    }

    @Override
    public void onAttachClicked(Official value, IsWidget widget) {
        attachmentService.getAttachmentsByCaseId(value.getId(), new RequestCallback<List<Attachment>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Attachment> list) {
                if (!list.isEmpty()) {
                    attachPopup.fill(list);
                    attachPopup.showNear(widget);
                }
            }
        });
    }

    @Override
    public void onRemoveClicked(Official value) {
        officialService.removeOfficial(value.getId(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errOfficialRemove(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                if (!result) {
                    fireEvent(new NotifyEvents.Show(lang.errOfficialRemove(), NotifyEvents.NotifyType.ERROR));
                    return;
                }
                requestTotalCount();
                showPreview(null);
            }
        });
    }

    private static String CREATE_ACTION;

    @Inject
    AttachPopup attachPopup;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    TableAnimation animation;
    @Inject
    OfficialControllerAsync officialService;
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
