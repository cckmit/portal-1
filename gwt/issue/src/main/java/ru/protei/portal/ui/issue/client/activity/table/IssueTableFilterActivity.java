package ru.protei.portal.ui.issue.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueCollapseFilterActivity;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueCollapseFilterView;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.activity.filter.IssueFilterService;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.attachment.popup.AttachPopup;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterWidget;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.common.CaseStateFilterProvider;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Collections;
import java.util.List;

/**
 * Активность таблицы обращений
 */
public abstract class IssueTableFilterActivity
        implements AbstractIssueTableActivity, AbstractPagerActivity, Activity, 
        AbstractIssueCollapseFilterActivity, AbstractIssueFilterModel
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.getIssueFilterParams().setModel(this);

        collapseFilterView.setActivity(this);
        collapseFilterView.getContainer().add(filterView.asWidget());
        view.getFilterContainer().add( collapseFilterView.asWidget() );
        pagerView.setActivity( this );

        toggleFilterCollapseState();
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
        filterView.presetFilterType();
        updateCaseStatesFilter();
        updateImportanceLevelButtons();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow( IssueEvents.Show event ) {
        applyFilterViewPrivileges();

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        filterView.showUserFilterControls();
        filterView.setCheckImportanceHistoryVisibility( false );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.ISSUE_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.ISSUE ) :
                new ActionBarEvents.Clear()
        );

        if(!policyService.hasSystemScopeForPrivilege( En_Privilege.COMPANY_VIEW ) ){
            filterView.getIssueFilterParams().presetCompany(policyService.getProfile().getCompany());
        }

        this.preScroll = event.preScroll;

        if (event.query != null) {
            fillFilterFieldsByCaseQuery(event.query);
            event.query = null;
        } else {
            loadTable();
        }

        validateSearchField(filterView.getIssueFilterParams().isSearchFieldCorrect());
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
        persistScroll();
        showPreview( value );
    }

    @Override
    public void onEditClicked( CaseShortView value ) {
        persistScroll();
        fireEvent(new IssueEvents.Edit(value.getCaseNumber()).withSource(this));
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
    public void onUserFilterChanged() {
        String validateString = filterView.getIssueFilterParams().validateMultiSelectorsTotalCount();
        if ( validateString != null ){
            fireEvent( new NotifyEvents.Show( lang.errTooMuchCompanies(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        boolean searchFieldCorrect = filterView.getIssueFilterParams().isSearchFieldCorrect();
        if(searchFieldCorrect) {
            loadTable();
        }
        validateSearchField(searchFieldCorrect);
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
                            restoreScroll();
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
        filterView.getIssueFilterParams().searchByCommentsWarningVisibility().setVisible(!isCorrect);
        filterView.createEnabled().setEnabled(isCorrect);
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private void persistScroll() {
        scrollTo = Window.getScrollTop();
    }

    private void restoreScroll() {
        if (!preScroll) {
            view.clearSelection();
            return;
        }

        Window.scrollTo(0, scrollTo);
        preScroll = false;
        scrollTo = 0;
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
                filterView.getIssueFilterParams().fillFilterFields(caseQuery, selectorsParams);
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
        return filterView.getIssueFilterParams().getFilterFields(En_CaseFilterType.CASE_OBJECTS);
    }

    private void applyFilterViewPrivileges() {
        filterView.getIssueFilterParams().productsVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_PRODUCT_VIEW ) );
        filterView.getIssueFilterParams().managersVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_MANAGER_VIEW ) );
        filterView.getIssueFilterParams().searchPrivateVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRIVACY_VIEW ) );
    }

    private void updateCaseStatesFilter() {
        if (!policyService.hasSystemScopeForPrivilege(En_Privilege.COMPANY_VIEW)) {
            filterView.getIssueFilterParams().setStateFilter(caseStateFilter.makeFilter(policyService.getUserCompany().getCaseStates()));
        }
    }

    private void updateImportanceLevelButtons() {
        if (!policyService.hasSystemScopeForPrivilege(En_Privilege.COMPANY_VIEW)) {
            filterView.getIssueFilterParams().fillImportanceButtons(Collections.emptyList());
            companyService.getImportanceLevels(policyService.getUserCompany().getId(), new FluentCallback<List<En_ImportanceLevel>>()
                    .withSuccess(importanceLevelList -> {
                        filterView.getIssueFilterParams().fillImportanceButtons(importanceLevelList);
                    }));
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

    @Inject
    Lang lang;

    @Inject
    AbstractIssueTableView view;

    @Inject
    AbstractIssueCollapseFilterView collapseFilterView;

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
    CompanyControllerAsync companyService;

    @Inject
    PolicyService policyService;

    @Inject
    CaseStateFilterProvider caseStateFilter;

    @Inject
    IssueFilterService issueFilterService;

    @Inject
    IssueFilterWidget filterView;

    private CaseQuery query = null;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
    private Integer scrollTo = 0;
    private Boolean preScroll = false;
}
