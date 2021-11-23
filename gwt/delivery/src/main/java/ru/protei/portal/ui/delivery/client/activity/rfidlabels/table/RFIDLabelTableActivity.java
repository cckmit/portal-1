package ru.protei.portal.ui.delivery.client.activity.rfidlabels.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.RFIDLabelEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RFIDLabelControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.widget.rfidlabel.filter.RFIDLabelFilterWidget;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public abstract class RFIDLabelTableActivity implements AbstractRFIDLabelTableActivity, AbstractPagerActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        pagerView.setActivity( this );

        filterWidget.onInit();
        filterWidget.setOnFilterChangeCallback(this::loadTable);
        view.getFilterContainer().add( filterWidget.asWidget() );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow( RFIDLabelEvents.Show event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        filterWidget.resetFilter();

        loadTable();
    }

    @Event
    public void onUpdate(RFIDLabelEvents.Change event) {
        if (view.asWidget().isAttached()) {
            view.updateRow(event.item);
        }
    }

    @Override
    public void onEditClicked(RFIDLabel value) {
        fireEvent(new RFIDLabelEvents.Edit(value.getId()));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<RFIDLabel>> asyncCallback) {
        boolean isFirstChunk = offset == 0;

        RFIDLabelQuery query = filterWidget.getFilterParamView().getQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        rfidLabelController.getByQuery(query, new FluentCallback<SearchResult<RFIDLabel>>()
                .withError(throwable -> {
                    errorHandler.accept(throwable);
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                    }
                    asyncCallback.onSuccess(sr.getResults());
                }));
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void onRemoveClicked(RFIDLabel value) {
        if (value == null) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.RFIDLabelsRemoveConfirmMessage(value.getEpc()),
                removeAction(value)));

    }

    private Runnable removeAction(RFIDLabel item) {
        return () -> rfidLabelController.remove(item, new FluentCallback<RFIDLabel>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.RFIDLabelsRemoveSucceeded(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new RFIDLabelEvents.Show());
                })
        );
    }

    private void loadTable() {
        view.clearRecords();
        view.triggerTableLoad();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractRFIDLabelTableView view;
    @Inject
    RFIDLabelControllerAsync rfidLabelController;
    @Inject
    DefaultErrorHandler errorHandler;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    RFIDLabelFilterWidget filterWidget;

    private AppEvents.InitDetails initDetails;
}
