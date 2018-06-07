package ru.protei.portal.ui.issue.client.activity.table;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.widget.attachment.popup.AttachPopup;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.issue.client.util.IssueFilterUtils;

import java.util.List;

/**
 * Активность таблицы обращений
 */
public abstract class IssueTableActivity
        implements AbstractIssueTableActivity, AbstractIssueFilterActivity,
        AbstractPagerActivity, Activity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );

        pagerView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( IssueEvents.Show event ) {
        applyFilterViewPrivileges();

        this.fireEvent( new AppEvents.InitPanelName( lang.issues() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        initDetails.parent.add( pagerView.asWidget() );
        showUserFilterControls();

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.ISSUE_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.ISSUE ) :
                new ActionBarEvents.Clear()
        );

        filterView.setReportButtonVisibility(policyService.hasPrivilegeFor(En_Privilege.ISSUE_EXPORT));

        requestIssuesCount();
    }

    @Event
    public void onChangeRow( IssueEvents.ChangeIssue event ) {
        issueService.getIssues( new CaseQuery(event.id), new RequestCallback<List<CaseShortView>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<CaseShortView> caseObjects ) {
                view.updateRow(caseObjects.get(0));
            }
        } );
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.ISSUE.equals( event.identity ) ) {
            return;
        }

        fireEvent(new IssueEvents.Edit());
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
        fireEvent(new IssueEvents.Edit(value.getCaseNumber(), null));
    }

    @Override
    public void onFilterChanged() {
        if ( !validateMultiSelectorsTotalCount() ){
            return;
        }
        requestIssuesCount();
    }

    @Override
    public void onSaveFilterClicked() {
        showUserFilterName();
    }

    @Override
    public void onFilterRemoveClicked( Long id ) {

        filterService.removeIssueFilter( id, new RequestCallback< Boolean >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotRemoved(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Boolean aBoolean ) {

                fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent( new IssueEvents.ChangeUserFilterModel() );
                filterView.resetFilter();
            }
        } );
    }

    @Override
    public void onUserFilterChanged() {

        CaseFilterShortView filter = filterView.userFilter().getValue();
        if (filter == null){
            filterView.resetFilter();
            showUserFilterControls();
            filterView.setSaveBtnLabel( lang.buttonCreate() );

            onFilterChanged();
            return;
        }

        filterView.setSaveBtnLabel( lang.buttonModify() );
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
    public void onOkSavingClicked() {
        if (filterView.filterName().getValue().isEmpty()){
            filterView.setFilterNameContainerErrorStyle( true );
            fireEvent( new NotifyEvents.Show( lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        boolean isNew = filterView.userFilter().getValue() == null;
        CaseFilter userFilter = fillUserFilter();
        if ( !isNew ){
            userFilter.setId( filterView.userFilter().getValue().getId() );
        }

        filterService.saveIssueFilter( userFilter, new RequestCallback< CaseFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseFilter filter ) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));

                CaseFilterShortView filterShortView = filter.toShortView();
                if ( isNew ){
                    filterView.userFilter().setValue( filterShortView );
                    filterView.addUserFilterDisplayOption( filterShortView );
                    filterView.setSaveBtnLabel( lang.buttonModify() );
                } else {
                    filterView.changeUserFilterValueName( filterShortView );
                }
                filterView.removeFilterBtnVisibility().setVisible( true );
                showUserFilterControls();
            }
        } );
    }

    @Override
    public void onCancelSavingClicked() {
        showUserFilterControls();
        filterView.resetFilter();
    }

    @Override
    public void onCreateReportClicked() {

        Report report = new Report();
        report.setCaseQuery(getQuery());
        report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
        if (!HelperFunc.isEmpty(filterView.filterName().getValue())) {
            report.setName(filterView.filterName().getValue());
        }

        reportService.createReport(report, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new NotifyEvents.Show(lang.reportRequested(), NotifyEvents.NotifyType.SUCCESS));
            }
        });
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<CaseShortView>> asyncCallback ) {
        CaseQuery query = getQuery();
        query.setOffset( offset );
        query.setLimit( limit );

        issueService.getIssues( query, new RequestCallback<List<CaseShortView>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                asyncCallback.onFailure( throwable );
            }

            @Override
            public void onSuccess( List<CaseShortView> caseObjects ) {
                asyncCallback.onSuccess( caseObjects );
            }
        } );
    }

    @Override
    public void onPageChanged( int page ) {
        pagerView.setCurrentPage( page+1 );
    }

    @Override
    public void onFirstClicked() {
        view.scrollTo( 0 );
    }

    @Override
    public void onLastClicked() {
        view.scrollTo( view.getPageCount()-1 );
    }

    @Override
    public void onAttachClicked(CaseShortView value, IsWidget widget) {
        attachmentService.getAttachmentsByCaseId(value.getId(), new RequestCallback<List<Attachment>>() {
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


    private void fillFilterFields( CaseFilter filter ) {

        filterView.removeFilterBtnVisibility().setVisible( true );
        filterView.filterName().setValue( filter.getName() );

        CaseQuery params = filter.getParams();
        filterView.searchPattern().setValue( params.getSearchString() );
        filterView.sortDir().setValue( params.getSortDir().equals( En_SortDir.ASC ) );
        filterView.sortField().setValue( params.getSortField() );
        filterView.dateRange().setValue( new DateInterval( params.getFrom(), params.getTo() ) );
        filterView.importances().setValue( IssueFilterUtils.getImportances( params.getImportanceIds() ) );
        filterView.states().setValue( IssueFilterUtils.getStates( params.getStateIds() ) );
        filterView.companies().setValue( IssueFilterUtils.getCompanies( params.getCompanyIds()) );
        filterView.managers().setValue( IssueFilterUtils.getManagers(params.getManagerIds()) );
        filterView.products().setValue( IssueFilterUtils.getProducts(params.getProductIds()) );
    }

    private void requestIssuesCount() {
        view.clearRecords();
        animation.closeDetails();

        issueService.getIssuesCount( getQuery(), new RequestCallback< Long >() {
                @Override
                public void onError( Throwable throwable ) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                }

                @Override
                public void onSuccess( Long issuesCount ) {
                    view.setIssuesCount( issuesCount );
                    pagerView.setTotalPages( view.getPageCount() );
                    pagerView.setTotalCount( issuesCount );
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
        CaseQuery query = new CaseQuery();
        query.setType( En_CaseType.CRM_SUPPORT );
        String value = filterView.searchPattern().getValue();

        if (value == null || value.isEmpty()) {
            query.setSearchString( null );
        }
        else {
            MatchResult result = caseNoPattern.exec( value );

            if (result != null && result.getGroup(0).equals( value )) {
                query.setCaseNo( Long.parseLong( value ) );
            }
            else {
                query.setSearchString( value );
            }
        }
        setQueryFields(query);
        return query;
    }

    private CaseFilter fillUserFilter() {

        CaseFilter filter = new CaseFilter();
        filter.setName( filterView.filterName().getValue() );
        CaseQuery query = new CaseQuery();
        filter.setParams( query );
        query.setSearchString( filterView.searchPattern().getValue() );
        setQueryFields(query);
        return filter;
    }

    private void setQueryFields( CaseQuery query ) {
        query.setSortField( filterView.sortField().getValue() );
        query.setSortDir( filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC );
        query.setCompanyIds( IssueFilterUtils.getCompaniesIdList( filterView.companies().getValue() ) );
        query.setProductIds( IssueFilterUtils.getProductsIdList( filterView.products().getValue() ) );
        query.setManagerIds( IssueFilterUtils.getManagersIdList( filterView.managers().getValue() ) );
        query.setImportanceIds( IssueFilterUtils.getImportancesIdList( filterView.importances().getValue() ) );
        query.setStates( IssueFilterUtils.getStateList( filterView.states().getValue() ) );

        DateInterval interval = filterView.dateRange().getValue();
        if ( interval != null ) {
            query.setFrom( interval.from );
            query.setTo( interval.to );
        }
    }

    private boolean validateMultiSelectorsTotalCount() {
        boolean isValid = true;
        if (filterView.companies().getValue().size() > 50){
            fireEvent( new NotifyEvents.Show( lang.errTooMuchCompanies(), NotifyEvents.NotifyType.ERROR ) );
            filterView.setCompaniesErrorStyle(true);
            isValid =  false;
        } else {
            filterView.setCompaniesErrorStyle(false);
        }
        if (filterView.products().getValue().size() > 50){
            fireEvent( new NotifyEvents.Show( lang.errTooMuchProducts(), NotifyEvents.NotifyType.ERROR ) );
            filterView.setProductsErrorStyle(true);
            isValid = false;
        } else {
            filterView.setProductsErrorStyle(false);
        }
        if (filterView.managers().getValue().size() > 50){
            fireEvent( new NotifyEvents.Show( lang.errTooMuchManagers(), NotifyEvents.NotifyType.ERROR ) );
            filterView.setManagersErrorStyle(true);
            isValid = false;
        } else {
            filterView.setManagersErrorStyle(false);
        }
        return isValid;
    }

    private void applyFilterViewPrivileges() {
        filterView.companiesVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_COMPANY_VIEW ) );
        filterView.productsVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_PRODUCT_VIEW ) );
        filterView.managersVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_MANAGER_VIEW ) );
    }

    private void showUserFilterName(){
        filterView.setUserFilterControlsVisibility(false);
        filterView.setUserFilterNameVisibility(true);
    }


    private void showUserFilterControls() {
        filterView.setUserFilterControlsVisibility(true);
        filterView.setUserFilterNameVisibility(false);
    }

    @Inject
    Lang lang;

    @Inject
    AbstractIssueTableView view;
    @Inject
    AbstractIssueFilterView filterView;

    @Inject
    IssueServiceAsync issueService;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractPagerView pagerView;

    @Inject
    AttachPopup attachPopup;

    @Inject
    AttachmentServiceAsync attachmentService;

    @Inject
    IssueFilterServiceAsync filterService;

    @Inject
    PolicyService policyService;

    @Inject
    ReportServiceAsync reportService;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;

    private final RegExp caseNoPattern = RegExp.compile("\\d+");
}
