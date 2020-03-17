package ru.protei.portal.ui.issue.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.filter.*;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.attachment.popup.AttachPopup;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.common.CaseStateFilterProvider;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.HashSet;
import java.util.List;

/**
 * Активность таблицы обращений
 */
public abstract class IssueTableActivity
        implements AbstractIssueTableActivity, AbstractPagerActivity, Activity, 
        AbstractIssueFilterActivity, AbstractIssueFilterParamActivity, AbstractIssueFilterCollapseActivity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        collapseFilterView.setActivity(this);

        filterView = collapseFilterView.getIssueFilterParamView();
        filterView.setActivity(this);

        filterParamView = filterView.getIssueFilterWidget();
        filterParamView.setActivity(this);
        filterParamView.setInitiatorModel(initiatorModel);
        filterParamView.setCreatorModel(personModel);
        filterParamView.setInitiatorCompaniesSupplier(() -> new HashSet<>( filterParamView.companies().getValue()));

        view.getFilterContainer().add( collapseFilterView.asWidget() );
        pagerView.setActivity( this );

        toggleFilterCollapseState();
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
        filterParamView.presetFilterType();
        updateCaseStatesFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow( IssueEvents.Show event ) {
        applyFilterViewPrivileges();

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );
        showUserFilterControls();

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.ISSUE_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.ISSUE ) :
                new ActionBarEvents.Clear()
        );

        if(!policyService.hasSystemScopeForPrivilege( En_Privilege.COMPANY_VIEW ) ){
            HashSet<EntityOption> companyIds = new HashSet<>();
            companyIds.add(IssueFilterUtils.toEntityOption(policyService.getProfile().getCompany()));
            filterParamView.companies().setValue( companyIds );
            filterParamView.updateInitiators();
        }

        clearScroll(event);

        if (event.query != null) {
            fillFilterFieldsByCaseQuery(event.query);
            event.query = null;
        } else {
            loadTable();
        }

        validateSearchField(isSearchFieldCorrect());
    }

    @Event
    public void onChangeRow( IssueEvents.ChangeIssue event ) {
        issueService.getIssues(new CaseQuery(event.id), new FluentCallback<SearchResult<CaseShortView>>()
                .withSuccess(sr -> {
                    view.updateRow(sr.getResults().get(0));
                }));
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.ISSUE.equals( event.identity ) ) {
            return;
        }

        view.clearSelection();

        fireEvent(new IssueEvents.Create());
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Override
    public void onItemClicked( CaseShortView value ) {
        showPreview( value );
    }

    @Override
    public void onEditClicked( CaseShortView value ) {
        persistScrollTopPosition();
        fireEvent(new IssueEvents.Edit(value.getCaseNumber()));
    }

    @Override
    public void onFilterCollapse() {
        animation.filterCollapse();
        issueFilterService.setFilterCollapsed(true);
    }

    @Override
    public void onFilterRestore() {
        animation.filterRestore();
        issueFilterService.setFilterCollapsed(false);
    }

    @Override
    public void onFilterChanged() {
        if ( !validateMultiSelectorsTotalCount() ){
            return;
        }

        if(isSearchFieldCorrect()) {
            loadTable();
        }
        validateSearchField(isSearchFieldCorrect());
    }

    @Override
    public void onSaveFilterClicked() {
        isCreateFilterAction = false;
        showUserFilterName();
    }

    @Override
    public void onCreateFilterClicked() {
        isCreateFilterAction = true;
        showUserFilterName();
    }

    @Override
    public void onFilterRemoveClicked( Long id ) {
        fireEvent(new ConfirmDialogEvents.Show(lang.issueFilterRemoveConfirmMessage(), removeAction(id)));
    }

    @Override
    public void onUserFilterChanged() {
        CaseFilterShortView filter = filterParamView.userFilter().getValue();
        if (filter == null){
            filterView.resetFilter();
            showUserFilterControls();

            onFilterChanged();
            return;
        }

        filterService.getIssueFilter( filter.getId(), new RequestCallback< CaseFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseFilter filter ) {
                fillFilterFields( filter );
                onFilterChanged();
            }
        } );
    }

    @Override
    public void onOkSavingFilterClicked() {
        if (filterView.filterName().getValue().isEmpty()){
            filterView.setFilterNameContainerErrorStyle( true );
            fireEvent( new NotifyEvents.Show( lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        CaseFilter userFilter = fillUserFilter();
        if ( !isCreateFilterAction ){
            userFilter.setId( filterParamView.userFilter().getValue().getId() );
        }

        filterService.saveIssueFilter( userFilter, new RequestCallback< CaseFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseFilter filter ) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new IssueEvents.ChangeUserFilterModel());

                filterView.editBtnVisibility().setVisible(true);
                filterView.removeFilterBtnVisibility().setVisible(true);

                filterParamView.userFilter().setValue(filter.toShortView());

                showUserFilterControls();
            }
        } );
    }

    @Override
    public void onCancelSavingFilterClicked() {
        showUserFilterControls();
    }

    @Override
    public void onCompaniesFilterChanged() {
        onFilterChanged();
        updateInitiatorSelector();
    }

    @Override
    public void loadData(int offset, int limit, final AsyncCallback<List<CaseShortView>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        issueService.getIssues(query, new FluentCallback<SearchResult<CaseShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (!query.equals(getQuery())) {
                        loadData(offset, limit, asyncCallback);
                    }
                    else {
                        asyncCallback.onSuccess(sr.getResults());
                        if (isFirstChunk) {
                            view.setTotalRecords(sr.getTotalCount());
                            pagerView.setTotalPages(view.getPageCount());
                            pagerView.setTotalCount(sr.getTotalCount());
                            restoreScrollTopPositionOrClearSelection();
                        }
                    }
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
    public void onAttachClicked(CaseShortView value, IsWidget widget) {
        attachmentService.getAttachmentsByCaseId(En_CaseType.CRM_SUPPORT, value.getId(), new RequestCallback<List<Attachment>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(List<Attachment> list) {
                if(!list.isEmpty()) {
                    attachPopup.fill(list);
                    attachPopup.showNear(widget);
                }
            }
        });
    }

    private void validateSearchField(boolean isCorrect){
        filterParamView.searchByCommentsWarningVisibility().setVisible(!isCorrect);
        filterView.createEnabled().setEnabled(isCorrect);
    }

    private boolean isSearchFieldCorrect(){
        return !filterParamView.searchByComments().getValue() ||
                filterParamView.searchPattern().getValue().length() >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS;
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private void persistScrollTopPosition() {
        scrollTop = Window.getScrollTop();
    }

    private void restoreScrollTopPositionOrClearSelection() {
        if (scrollTop == null) {
            view.clearSelection();
            return;
        }
        int trh = RootPanel.get(DebugIds.DEBUG_ID_PREFIX + DebugIds.APP_VIEW.GLOBAL_CONTAINER).getOffsetHeight() - Window.getClientHeight();
        if (scrollTop <= trh) {
            Window.scrollTo(0, scrollTop);
            scrollTop = null;
        }
    }

    private void fillFilterFields( CaseFilter filter ) {
        filterView.removeFilterBtnVisibility().setVisible( true );
        filterView.editBtnVisibility().setVisible( true );
        filterView.filterName().setValue( filter.getName() );
        filterParamView.fillFilterFields(filter.getParams(), filter.getSelectorsParams());
    }

    private void fillFilterFieldsByCaseQuery( CaseQuery caseQuery ) {
        filterView.resetFilter();
        filterService.getSelectorsParams( caseQuery, new RequestCallback<SelectorsParams>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( SelectorsParams selectorsParams ) {
                filterParamView.fillFilterFields(caseQuery, selectorsParams);
                onFilterChanged();
            }
        } );
    }

    private void showPreview ( CaseShortView value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new IssueEvents.ShowPreview( view.getPreviewContainer(), value.getCaseNumber() ) );
        }
    }

    private CaseQuery getQuery() {
        return IssueFilterUtils.makeCaseQuery(filterParamView);
    }

    private CaseFilter fillUserFilter() {
        CaseFilter filter = new CaseFilter();
        filter.setName(filterView.filterName().getValue());
        filter.setType(En_CaseFilterType.CASE_OBJECTS);
        CaseQuery query = IssueFilterUtils.makeCaseQuery(filterParamView);
        filter.setParams(query);
        query.setSearchString(filterParamView.searchPattern().getValue());
        return filter;
    }

    private boolean validateMultiSelectorsTotalCount() {
        boolean isValid = true;
        if (filterParamView.companies().getValue().size() > 50){
            fireEvent( new NotifyEvents.Show( lang.errTooMuchCompanies(), NotifyEvents.NotifyType.ERROR ) );
            filterParamView.setCompaniesErrorStyle(true);
            isValid =  false;
        } else {
            filterParamView.setCompaniesErrorStyle(false);
        }
        if (filterParamView.products().getValue().size() > 50){
            fireEvent( new NotifyEvents.Show( lang.errTooMuchProducts(), NotifyEvents.NotifyType.ERROR ) );
            filterParamView.setProductsErrorStyle(true);
            isValid = false;
        } else {
            filterParamView.setProductsErrorStyle(false);
        }
        if (filterParamView.managers().getValue().size() > 50){
            fireEvent( new NotifyEvents.Show( lang.errTooMuchManagers(), NotifyEvents.NotifyType.ERROR ) );
            filterParamView.setManagersErrorStyle(true);
            isValid = false;
        }
        if (filterParamView.initiators().getValue().size() > 50){
            fireEvent( new NotifyEvents.Show( lang.errTooMuchInitiators(), NotifyEvents.NotifyType.ERROR ) );
            filterParamView.setInitiatorsErrorStyle(true);
            isValid = false;
        } else {
            filterParamView.setManagersErrorStyle(false);
        }
        return isValid;
    }

    private void applyFilterViewPrivileges() {
        filterParamView.productsVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_PRODUCT_VIEW ) );
        filterParamView.managersVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_MANAGER_VIEW ) );
        filterParamView.searchPrivateVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRIVACY_VIEW ) );
    }

    private void showUserFilterName(){
        filterView.setUserFilterControlsVisibility(false);
        filterView.setUserFilterNameVisibility(true);
    }

    private void showUserFilterControls() {
        filterView.setUserFilterControlsVisibility(true);
        filterView.setUserFilterNameVisibility(false);
    }

    private void updateCaseStatesFilter() {
        if (!policyService.hasSystemScopeForPrivilege(En_Privilege.COMPANY_VIEW)) {
            filterParamView.setStateFilter(caseStateFilter.makeFilter(policyService.getUserCompany().getCaseStates()));
        }
    }

    private void toggleFilterCollapseState() {
        Boolean isCollapsed = issueFilterService.isFilterCollapsed();
        if (isCollapsed == null) {
            return;
        }
        if (isCollapsed) {
            animation.filterCollapse();
        } else {
            animation.filterRestore();
        }
    }

    private void updateInitiatorSelector() {
        filterParamView.updateInitiators();
    }

    private void clearScroll(IssueEvents.Show event) {
        if (event.clearScroll) {
            event.clearScroll = false;
            this.scrollTop = null;
        }
    }

    private Runnable removeAction(Long filterId) {
        return () -> filterService.removeIssueFilter(filterId, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new IssueEvents.ChangeUserFilterModel());
                filterView.resetFilter();
                loadTable();
            }
        });
    }

    @Inject
    Lang lang;

    @Inject
    AbstractIssueTableView view;
    @Inject
    AbstractIssueCollapseFilterView collapseFilterView;

    AbstractIssueFilterView filterView;

    @Inject
    IssueControllerAsync issueService;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractPagerView pagerView;

    @Inject
    AttachPopup attachPopup;

    @Inject
    AttachmentServiceAsync attachmentService;

    @Inject
    IssueFilterControllerAsync filterService;

    @Inject
    PolicyService policyService;

    @Inject
    CaseStateFilterProvider caseStateFilter;

    @Inject
    IssueFilterService issueFilterService;

    @Inject
    InitiatorModel initiatorModel;

    @Inject
    PersonModel personModel;

    private CaseQuery query = null;

    private static String CREATE_ACTION;
    private AbstractIssueFilterWidgetView filterParamView;
    private AppEvents.InitDetails initDetails;
    private Integer scrollTop;
    private boolean isCreateFilterAction = true;
}
