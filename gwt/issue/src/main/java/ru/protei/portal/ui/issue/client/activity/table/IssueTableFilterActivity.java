package ru.protei.portal.ui.issue.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;
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
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.widget.attachment.popup.AttachPopup;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterWidget;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CustomerCompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.ManagerCompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.SubcontractorCompanyModel;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.common.CaseStateFilterProvider;
import ru.protei.winter.core.utils.beans.SearchResult;

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
        filterView.getIssueFilterParams().presetDefaultModifiedRange(DEFAULT_MODIFIED_RANGE);

        collapseFilterView.setActivity(this);
        collapseFilterView.getContainer().add(filterView.asWidget());
        view.getFilterContainer().add( collapseFilterView.asWidget() );
        pagerView.setActivity( this );

        view.setChangeSelectionIfSelectedPredicate(caseShortView -> animation.isPreviewShow());

        toggleFilterCollapseState();
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_VIEW)) {
            return;
        }

        filterView.resetFilter();
        filterView.presetFilterType();
        updateCaseStatesFilter();
        updateCompanyModels(event.profile);
    }

    @Event(Type.FILL_CONTENT)
    public void onShow( IssueEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        applyFilterViewPrivileges();

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        filterView.showUserFilterControls();

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.ISSUE_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.ISSUE ) :
                new ActionBarEvents.Clear()
        );
        if (!policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW)) {
            if (policyService.isSubcontractorCompany()) {
                filterView.getIssueFilterParams().presetManagerCompany(policyService.getProfile().getCompany());
            } else {
                filterView.getIssueFilterParams().presetCompany(policyService.getProfile().getCompany());
            }
        } else {
            companyService.getSingleHomeCompanies(new FluentCallback<List<EntityOption>>()
                    .withSuccess(companies -> {
                        filterView.getIssueFilterParams().presetManagerCompanies(companies);
                    })
            );
        }

        this.preScroll = event.preScroll;

        if (event.caseFilterDto == null || event.caseFilterDto.getQuery() == null) {
            loadTable();
        } else {
            fillFilterFieldsByCaseQuery(event.caseFilterDto);
        }

        validateSearchField(filterView.getIssueFilterParams().isSearchFieldCorrect());
        validateCreatedRange(filterView.getIssueFilterParams().isCreatedRangeValid());
        validateModifiedRange(filterView.getIssueFilterParams().isModifiedRangeValid());
    }

    @Event
    public void onChangeRow( IssueEvents.ChangeIssue event ) {
        issueService.getIssues(new CaseQuery(event.id), new FluentCallback<SearchResult<CaseShortView>>()
                .withSuccess(sr -> view.updateRow(sr.getResults().get(0)))
        );
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
    public void onItemClicked(CaseShortView value ) {
        persistScroll();
        showPreview( value );
    }

    @Override
    public void onEditClicked( CaseShortView value ) {
        persistScroll();
        fireEvent(new IssueEvents.Edit(value.getCaseNumber()).withBackHandler(() -> fireEvent(new IssueEvents.Show(true))));
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
        boolean createdRangeValid = filterView.getIssueFilterParams().isCreatedRangeValid();
        boolean modifiedRangeValid = filterView.getIssueFilterParams().isModifiedRangeValid();

        if(searchFieldCorrect && createdRangeValid && modifiedRangeValid) {
            loadTable();
        }
        validateSearchField(searchFieldCorrect);
        validateCreatedRange(createdRangeValid);
        validateModifiedRange(modifiedRangeValid);
    }

    @Override
    public void onPlanPresent(boolean isPresent) {
        if (isPresent) {
            filterView.getIssueFilterParams().sortField().setValue(En_SortField.by_plan);
            filterView.getIssueFilterParams().sortDir().setValue(true);
            filterView.getIssueFilterParams().resetRanges();
            fireEvent(new NotifyEvents.Show(lang.reportCaseObjectPlanInfo(), NotifyEvents.NotifyType.INFO));
        } else {
            filterView.getIssueFilterParams().sortField().setValue(En_SortField.issue_number);
            filterView.getIssueFilterParams().sortDir().setValue(false);
        }
    }

    @Override
    public void loadData(int offset, int limit, final AsyncCallback<List<CaseShortView>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        if (isFirstChunk) {
            showLoading(true);
        }
        query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        issueService.getIssues(query, new FluentCallback<SearchResult<CaseShortView>>()
                .withError(throwable -> {
                    showLoading(false);
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    showLoading(false);
                    if (!query.equals(getQuery())) {
                        loadData(offset, limit, asyncCallback);
                    }
                    else {
                        if (isFirstChunk) {
                            view.setTotalRecords(sr.getTotalCount());
                            pagerView.setTotalPages(view.getPageCount());
                            pagerView.setTotalCount(sr.getTotalCount());
                            restoreScroll();
                        }
                        asyncCallback.onSuccess(sr.getResults());
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

    @Override
    public boolean isFavoriteItem(CaseShortView value) {
        if (value == null) {
            return false;
        }

        return value.isFavorite();
    }

    @Override
    public void onFavoriteStateChanged(final CaseShortView value) {
        if (value == null) {
            return;
        }

        if (value.isFavorite()) {
            issueService.removeFavoriteState(policyService.getProfileId(), value.getId(), new FluentCallback<Long>()
                    .withSuccess(result -> onSuccessChangeFavoriteState(value, view))
            );
        } else {
            issueService.addFavoriteState(policyService.getProfileId(), value.getId(), new FluentCallback<Long>()
                    .withSuccess(result -> onSuccessChangeFavoriteState(value, view))
            );
        }
    }

    private void onSuccessChangeFavoriteState(CaseShortView value, AbstractIssueTableView view) {
        value.setFavorite(!value.isFavorite());

        view.updateRow(value);

        fireEvent(new IssueEvents.IssueFavoriteStateChanged(value.getId(), value.isFavorite()));
        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
    }

    private void validateSearchField(boolean isCorrect){
        filterView.getIssueFilterParams().searchByCommentsWarningVisibility().setVisible(!isCorrect);
        filterView.createEnabled().setEnabled(isCorrect);
    }

    private void validateCreatedRange(boolean isValid){
        filterView.getIssueFilterParams().setCreatedRangeValid(true, isValid);
        filterView.createEnabled().setEnabled(isValid);
    }

    private void validateModifiedRange(boolean isValid){
        filterView.getIssueFilterParams().setModifiedRangeValid(true, isValid);
        filterView.createEnabled().setEnabled(isValid);
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

    private void fillFilterFieldsByCaseQuery(CaseFilterDto<CaseQuery> caseFilterDto) {
        filterView.resetFilter();
        filterView.userFilter().setValue(caseFilterDto.getCaseFilter().toShortView());

        final CaseQuery caseQuery = caseFilterDto.getQuery();

        filterService.getSelectorsParams(caseQuery, new RequestCallback<SelectorsParams>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(SelectorsParams selectorsParams) {
                filterView.getIssueFilterParams().fillFilterFields(caseQuery, selectorsParams);
            }
        });
    }

    private void showPreview ( CaseShortView value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new IssueEvents.ShowPreview( view.getPreviewContainer(), value.getCaseNumber() ).withBackHandler(() -> fireEvent(new IssueEvents.Show(true))) );
        }
    }

    private CaseQuery getQuery() {
        return filterView.getIssueFilterParams().getFilterFields(En_CaseFilterType.CASE_OBJECTS);
    }

    private void applyFilterViewPrivileges() {
        filterView.getIssueFilterParams().productsVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_PRODUCT_VIEW ) );
        filterView.getIssueFilterParams().searchPrivateVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRIVACY_VIEW ) );
        filterView.getIssueFilterParams().planVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PLAN_VIEW));
        filterView.getIssueFilterParams().creatorsVisibility().setVisible(policyService.personBelongsToHomeCompany());
        filterView.getIssueFilterParams().initiatorsVisibility().setVisible(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) || !policyService.isSubcontractorCompany());
        filterView.getIssueFilterParams().managersVisibility().setVisible(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) || policyService.isSubcontractorCompany());
    }

    private void updateCaseStatesFilter() {
        if (!policyService.hasSystemScopeForPrivilege(En_Privilege.COMPANY_VIEW)) {
            filterView.getIssueFilterParams().setStateFilter(caseStateFilter.makeFilter(policyService.getUserCompany().getCaseStates()));
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

    private void updateCompanyModels(Profile profile) {
        Company userCompany = profile.getCompany();
        subcontractorCompanyModel.setCompanyId(userCompany.getId());
        subcontractorCompanyModel.setActive(false);
        customerCompanyModel.setSubcontractorId(userCompany.getId());
        customerCompanyModel.setActive(false);

        filterView.setInitiatorCompaniesModel(isSubcontractorCompany(userCompany) ? customerCompanyModel : companyModel);
        filterView.setManagerCompaniesModel(profile.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) || isSubcontractorCompany(userCompany) ? managerCompanyModel : subcontractorCompanyModel);
    }

    private boolean isSubcontractorCompany(Company userCompany) {
        return userCompany.getCategory() == En_CompanyCategory.SUBCONTRACTOR;
    }

    private void showLoading(boolean isShow) {
        view.loadingVisibility().setVisible(isShow);
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
    AttachmentControllerAsync attachmentService;

    @Inject
    CaseFilterControllerAsync filterService;

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

    @Inject
    CompanyModel companyModel;

    @Inject
    CustomerCompanyModel customerCompanyModel;

    @Inject
    ManagerCompanyModel managerCompanyModel;

    @Inject
    SubcontractorCompanyModel subcontractorCompanyModel;

    private CaseQuery query = null;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
    private Integer scrollTo = 0;
    private Boolean preScroll = false;
    private final DateIntervalWithType DEFAULT_MODIFIED_RANGE = new DateIntervalWithType(null, En_DateIntervalType.PREVIOUS_AND_THIS_MONTH);
}
