package ru.protei.portal.ui.document.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterActivity;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterView;

import java.util.List;


public abstract class DocumentTableActivity
        implements Activity, AbstractDocumentTableActivity, AbstractDocumentFilterActivity,
        AbstractPagerActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setAnimation(animation);

        CREATE_ACTION = lang.buttonCreate();
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());

        pagerView.setPageSize(view.getPageSize());
        pagerView.setActivity(this);

    }

    @Event
    public void onShow(DocumentEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());
        init.parent.add(pagerView.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_CREATE) ?
                new ActionBarEvents.Add(CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.DOCUMENT) :
                new ActionBarEvents.Clear()
        );

        query = makeQuery();
        requestTotalCount();
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page + 1);
    }

    @Override
    public void onFirstClicked() {
        view.scrollTo(0);
    }

    @Override
    public void onLastClicked() {
        view.scrollTo(view.getPageCount() - 1);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.init = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DOCUMENT.equals(event.identity)) {
            return;
        }
        fireEvent(new DocumentEvents.Edit());
    }

    @Override
    public void onEditClicked(Document value) {
        fireEvent(DocumentEvents.Edit.byId(value.getId()));
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        requestTotalCount();
    }

    @Override
    public void onItemClicked(Document value) {
        showPreview(value);
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Document>> callback) {
        query.setOffset(offset);
        query.setLimit(limit);

        documentService.getDocuments(query, new RequestCallback<List<Document>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(List<Document> result) {
                callback.onSuccess(result);
            }
        });
    }

    private void requestTotalCount() {
        view.clearRecords();

        documentService.getDocumentCount(query, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                view.setRecordCount(result);
                pagerView.setTotalPages(view.getPageCount());
            }
        });
    }

    private void showPreview(Document document) {
        if (document == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new DocumentEvents.ShowPreview(view.getPreviewContainer(), document));
        }
    }

    private DocumentQuery makeQuery() {
        Long managerId = filterView.manager().getValue() == null ? null : filterView.manager().getValue().getId();
        DateInterval interval = filterView.dateRange().getValue();

        return new DocumentQuery(
                filterView.name().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC,
                filterView.organizationCodes().getValue(),
                filterView.documentType().getValue(),
                (interval == null ? null : interval.from),
                (interval == null ? null : interval.to),
                filterView.keywords().getValue(),
                managerId,
                filterView.content().getValue()
        );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentTableView view;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    AbstractDocumentFilterView filterView;
    @Inject
    TableAnimation animation;
    @Inject
    DocumentServiceAsync documentService;
    @Inject
    PolicyService policyService;


    private static String CREATE_ACTION;
    private AppEvents.InitDetails init;
    private DocumentQuery query;
}
