package ru.protei.portal.ui.document.client.activity.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DocumentState;
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
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterActivity;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.function.Consumer;


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

        pagerView.setActivity(this);

    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DocumentEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());
        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_CREATE) ?
                new ActionBarEvents.Add(CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.DOCUMENT) :
                new ActionBarEvents.Clear()
        );

        query = makeQuery();
        loadTable();
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
    public void onDownloadClicked(Document value) {
        if (value.getId() == null || value.getProjectId() == null)
            return;
        Window.open(GWT.getModuleBaseURL() + DOWNLOAD_PATH + value.getProjectId() + "/" + value.getId(), value.getName(), "");
    }

    @Override
    public void onArchiveClicked(Document value) {
        if (value == null) {
            return;
        }

        documentService.changeState(value.getId(), value.getState() == En_DocumentState.DEPRECATED ? En_DocumentState.ACTIVE : En_DocumentState.DEPRECATED, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Boolean result) {
                loadTable();
                fireEvent(new NotifyEvents.Show(lang.msgStatusChanged(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new DocumentEvents.ChangeModel());
            }
        });
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
        if (!value.isDeprecatedUnit()) {
            fireEvent(DocumentEvents.Edit.byId(value.getId()));
        }
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        loadTable();
    }

    @Override
    public void onItemClicked(Document value) {
        showPreview(value);
    }

    @Override
    public void onProjectColumnClicked(Document value) {
        if (value == null || value.getProjectInfo() == null)
            return;
        fireEvent(new ProjectEvents.Show());
        fireEvent(new ProjectEvents.ShowFullScreen(value.getProjectInfo().getId()));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Document>> callback) {
        boolean isFirstChunk = offset == 0;
        query.setOffset(offset);
        query.setLimit(limit);
        documentService.getDocuments(query, new FluentCallback<SearchResult<Document>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    callback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    callback.onSuccess(sr.getResults());
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                    }
                }));
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
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
                filterView.content().getValue(),
                filterView.approved().getValue(),
                filterView.showDeprecated().getValue() ? null : En_DocumentState.ACTIVE
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
    DocumentControllerAsync documentService;
    @Inject
    PolicyService policyService;


    private static String CREATE_ACTION;
    private AppEvents.InitDetails init;
    private DocumentQuery query;

    private static final String DOWNLOAD_PATH = "springApi/document/";
}
