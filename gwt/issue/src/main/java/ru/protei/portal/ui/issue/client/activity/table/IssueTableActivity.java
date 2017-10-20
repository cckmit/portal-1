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
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.client.widget.attachment.popup.AttachPopup;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.List;
import java.util.stream.Collectors;

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
    public void onCreateClicked( SectionEvents.Clicked event ) {
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
        fireEvent(new IssueEvents.Edit(value.getId(), null));
    }

    @Override
    public void onFilterChanged() {
        requestIssuesCount();
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
            fireEvent( new IssueEvents.ShowPreview( view.getPreviewContainer(), value.getId() ) );
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

        query.setCompanyId( getFilterCompanyId() );
        query.setProductId( filterView.product().getValue() == null ? null : filterView.product().getValue().getId() );
        query.setManagerId( filterView.manager().getValue() == null ? null : filterView.manager().getValue().getId() );

        if(filterView.states().getValue() != null)
            query.setStateIds(
                    filterView.states().getValue()
                            .stream()
                            .map( En_CaseState::getId )
                            .collect( Collectors.toList() ));

        if(filterView.importances().getValue() != null)
            query.setImportanceIds(
                    filterView.importances().getValue()
                            .stream()
                            .map( En_ImportanceLevel::getId )
                            .collect( Collectors.toList() ));

        DateInterval interval = filterView.dateRange().getValue();

        if(interval != null) {
            query.setFrom( interval.from );
            query.setTo( interval.to );
        }

        return query;
    }

    private void applyFilterViewPrivileges() {
        filterView.companyEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_COMPANY_EDIT ) );
        filterView.productEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRODUCT_EDIT ) );
        filterView.managerEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_MANAGER_EDIT ) );

        if ( !policyService.hasPrivilegeFor( En_Privilege.ISSUE_COMPANY_EDIT ) ) {
            Company userCompany = policyService.getUserCompany();
            filterView.company().setValue(userCompany == null ? null : userCompany.toEntityOption());
        }
    }

    private Long getFilterCompanyId() {
        if ( !policyService.hasPrivilegeFor( En_Privilege.ISSUE_COMPANY_EDIT ) ) {
            Company userCompany = policyService.getUserCompany();
            return userCompany == null ? null : userCompany.getId();
        }

        return filterView.company().getValue() == null ? null : filterView.company().getValue().getId();
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
    PolicyService policyService;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;

    private final RegExp caseNoPattern = RegExp.compile("\\d+");
}
