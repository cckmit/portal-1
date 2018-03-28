package ru.protei.portal.ui.issue.client.activity.table;

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
import ru.protei.portal.core.model.ent.IssueFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        pagerView.setPageSize( view.getPageSize() );
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

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.ISSUE_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.ISSUE ) :
                new ActionBarEvents.Clear()
        );
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
        requestIssuesCount();
    }

    @Override
    public void onSaveFilterClicked() {

        if (filterView.filterName().getValue().isEmpty()){
            filterView.setFilterNameContainerErrorStyle( true );
            fireEvent( new NotifyEvents.Show( lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        filterService.saveIssueFilter( fillFilter(), new RequestCallback< IssueFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( IssueFilter filter ) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent( new IssueEvents.ChangeUserFilterModel() );
                filterView.userFilter().setValue( filter.toShortView() );
                filterView.removeFilterBtnVisibility().setVisible( true );
                filterView.setFilterNameContainerVisibility( true );
            }
        } );
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

        Long filterId = filterView.userFilter().getValue().getId();
        filterService.getIssueFilter( filterId, new RequestCallback< IssueFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( IssueFilter filter ) {
                fillFilterFields( filter );
            }
        } );
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


    private void fillFilterFields( IssueFilter filter ) {

        filterView.removeFilterBtnVisibility().setVisible( true );
        filterView.setFilterNameContainerVisibility( true );
        filterView.filterName().setValue( filter.getName() );

        CaseQuery params = filter.getParams();
        filterView.searchPattern().setValue( params.getSearchString() );
        filterView.sortDir().setValue( params.getSortDir().equals( En_SortDir.ASC ) );
        filterView.dateRange().getValue().from = params.getFrom();
        filterView.dateRange().getValue().to = params.getTo();
        filterView.importances().setValue( getImportances( params.getImportanceIds() ) );
        filterView.states().setValue( getStates( params.getStateIds() ) );
        filterView.companies().setValue( getCompanies( params.getCompanyIds()) );
        filterView.managers().setValue( getManagers(params.getManagerIds()) );
        filterView.products().setValue( getProducts(params.getProductIds()) );
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

        query.setSortField( filterView.sortField().getValue() );
        query.setSortDir( filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC );

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

        query.setCompanyIds( getCompaniesIdList() );
        query.setProductIds( getProductsIdList());
        query.setManagerIds( getManagersIdList() );

        query.setImportanceIds( getImportancesIdList() );
        query.setStates( new ArrayList<>( filterView.states().getValue() ) );

        DateInterval interval = filterView.dateRange().getValue();

        if(interval != null) {
            query.setFrom( interval.from );
            query.setTo( interval.to );
        }

        return query;
    }


    private IssueFilter fillFilter() {

        IssueFilter filter = new IssueFilter();
        filter.setName( filterView.filterName().getValue() );
        CaseQuery params = new CaseQuery();
        filter.setParams( params );

        params.setCompanyIds( getCompaniesIdList());

        params.setProductIds( getProductsIdList() );

        params.setManagerIds( getManagersIdList() );

        params.setFrom( filterView.dateRange().getValue().from );
        params.setTo( filterView.dateRange().getValue().to );
        params.setSearchString( filterView.searchPattern().getValue() );

        params.setImportanceIds( getImportancesIdList() );
        params.setStates( getStateList() );

        params.setSortDir( filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC );

        return filter;
    }

    private List< Integer > getImportancesIdList() {

        if ( filterView.importances().getValue() == null || filterView.importances().getValue().isEmpty() ) {
            return null;
        }
        //TODO CRM-93 use stream
//        return filterView.importances().getValue()
//                .stream()
//                .map( En_ImportanceLevel::getId )
//                .collect( Collectors.toList() );

        List< Integer > importances = new ArrayList< Integer >();
        for ( En_ImportanceLevel option : filterView.importances().getValue() ) {
            importances.add( option.getId() );
        }
        return importances;
    }

    private Set< En_ImportanceLevel > getImportances( List< Integer > importancesIdList ) {
        if ( importancesIdList == null || importancesIdList.isEmpty() ) {
            return null;
        }

        //TODO CRM-93 use stream
//        return importancesIdList
//                .stream()
//                .map( En_ImportanceLevel::getById )
//                .collect( Collectors.toSet() );

        Set< En_ImportanceLevel > importances = new HashSet<>();
        for ( Integer id : importancesIdList ) {
            importances.add( En_ImportanceLevel.getById( id ) );
        }
        return importances;
    }

    private Set< En_CaseState > getStates( List< Integer > statesIdList ) {
        if ( statesIdList == null || statesIdList.isEmpty() ) {
            return null;
        }

        //TODO CRM-93 use stream
//        return importancesIdList
//                .stream()
//                .map( En_ImportanceLevel::getById )
//                .collect( Collectors.toSet() );

        Set< En_CaseState > states = new HashSet<>();
        for ( Integer id : statesIdList ) {
            states.add( En_CaseState.getById( Long.valueOf( id ) ) );
        }
        return states;
    }

    private List<En_CaseState> getStateList(){

        if(filterView.states().getValue() == null || filterView.states().getValue().isEmpty()){
            return null;
        }
        //TODO CRM-93 use stream
//        return filterView.states().getValue()
//                .stream()
//                .map( En_CaseState::getId )
//                .collect( Collectors.toList() );

        return new ArrayList(filterView.states().getValue());
    }

    private List< Long > getCompaniesIdList() {

        if ( filterView.companies().getValue() == null || filterView.companies().getValue().isEmpty() ) {
            return null;
        }
        //TODO CRM-93 use stream
        //            query.setCompanyIds(
//                    filterView.products().getValue()
//                            .stream()
//                            .map( ProductShortView::getId )
//                            .collect( Collectors.toList() ) );
        List< Long > companies = new ArrayList< Long >();
        for ( EntityOption option : filterView.companies().getValue() ) {
            companies.add( option.getId() );
        }
        return companies;
    }

    private Set< EntityOption > getCompanies( List< Long > companyIds ) {

        if ( companyIds == null || companyIds.isEmpty() ) {
            return null;
        }
        //TODO CRM-93 use stream
        //            query.setCompanyIds(
//                    filterView.products().getValue()
//                            .stream()
//                            .map( ProductShortView::getId )
//                            .collect( Collectors.toList() ) );
        Set< EntityOption > companies = new HashSet<>();
        for ( Long id : companyIds ) {
            companies.add( new EntityOption( "", id ) );
        }
        return companies;
    }

    private List<Long> getProductsIdList(){

        if ( filterView.products().getValue() == null || filterView.products().getValue().isEmpty() ) {
            return null;
        }
        //TODO CRM-93 use stream
        //            query.setProductIds(
//                    filterView.products().getValue()
//                            .stream()
//                            .map( ProductShortView::getId )
//                            .collect( Collectors.toList() ) );
        List< Long > products = new ArrayList< Long >();
        for ( ProductShortView prd : filterView.products().getValue() ) {
            products.add( prd.getId() );
        }
        return products;
    }

    private Set<ProductShortView> getProducts( List< Long > managerIds ){

        if ( managerIds == null || managerIds.isEmpty() ) {
            return null;
        }
        //TODO CRM-93 use stream
        //            query.setProductIds(
//                    filterView.products().getValue()
//                            .stream()
//                            .map( ProductShortView::getId )
//                            .collect( Collectors.toList() ) );
        Set< ProductShortView > products = new HashSet<>();
        for ( Long id : managerIds ) {
            ProductShortView prd = new ProductShortView();
            prd.setId( id );
            products.add( prd );
        }
        return products;
    }

    private List< Long > getManagersIdList() {

        if ( filterView.managers().getValue() == null || filterView.managers().getValue().isEmpty() ) {
            return null;
        }
        //TODO CRM-93 use stream
        //            query.setManagerIds(
//                    filterView.managers().getValue()
//                            .stream()
//                            .map( PersonShortView::getId )
//                            .collect( Collectors.toList() ) );
        List< Long > managers = new ArrayList< Long >();
        for ( PersonShortView manager : filterView.managers().getValue() ) {
            managers.add( manager.getId() );
        }
        return managers;
    }

    private Set<PersonShortView> getManagers( List< Long > managerIds ){

        if ( managerIds == null || managerIds.isEmpty() ) {
            return null;
        }
        //TODO CRM-93 use stream
        //            query.setProductIds(
//                    filterView.products().getValue()
//                            .stream()
//                            .map( ProductShortView::getId )
//                            .collect( Collectors.toList() ) );
        Set< PersonShortView > managers = new HashSet<>();
        for ( Long id : managerIds ) {
            PersonShortView person = new PersonShortView();
            person.setId( id );
            managers.add( person );
        }
        return managers;
    }

    private void applyFilterViewPrivileges() {
        filterView.companiesVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_COMPANY_VIEW ) );
        filterView.productsVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_PRODUCT_VIEW ) );
        filterView.managersVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_MANAGER_VIEW ) );
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

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;

    private final RegExp caseNoPattern = RegExp.compile("\\d+");
}
