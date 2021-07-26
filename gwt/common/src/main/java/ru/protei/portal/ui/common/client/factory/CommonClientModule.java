package ru.protei.portal.ui.common.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.activity.actionbar.ActionBarActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemListActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;
import ru.protei.portal.ui.common.client.activity.casecomment.list.CaseCommentItemListActivity;
import ru.protei.portal.ui.common.client.activity.casehistory.CaseHistoryItemListActivity;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemView;
import ru.protei.portal.ui.common.client.activity.caselink.list.AbstractCaseLinkListView;
import ru.protei.portal.ui.common.client.activity.caselink.list.CaseLinkListActivity;
import ru.protei.portal.ui.common.client.activity.casetag.edit.AbstractCaseTagEditView;
import ru.protei.portal.ui.common.client.activity.casetag.edit.CaseTagEditActivity;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListView;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.CaseTagListActivity;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.CaseTagListSingletonActivity;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.item.AbstractCaseTagItemView;
import ru.protei.portal.ui.common.client.activity.casetag.tagselector.CaseTagSelectorActivity;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;
import ru.protei.portal.ui.common.client.activity.commenthistory.CommentAndHistoryListActivity;
import ru.protei.portal.ui.common.client.activity.companydepartment.edit.AbstractCompanyDepartmentEditView;
import ru.protei.portal.ui.common.client.activity.companydepartment.edit.CompanyDepartmentEditActivity;
import ru.protei.portal.ui.common.client.activity.confirmdialog.AbstractConfirmDialogView;
import ru.protei.portal.ui.common.client.activity.confirmdialog.ConfirmDialogActivity;
import ru.protei.portal.ui.common.client.activity.contactitem.AbstractContactItemListView;
import ru.protei.portal.ui.common.client.activity.contactitem.AbstractContactItemView;
import ru.protei.portal.ui.common.client.activity.contactitem.ContactItemActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.errorpage.AbstractErrorPageView;
import ru.protei.portal.ui.common.client.activity.errorpage.ErrorPageActivity;
import ru.protei.portal.ui.common.client.activity.externallink.ExternalLinkActivity;
import ru.protei.portal.ui.common.client.activity.filter.IssueFilterWidgetModel;
import ru.protei.portal.ui.common.client.activity.info.AbstractJiraInfoActivity;
import ru.protei.portal.ui.common.client.activity.info.AbstractJiraInfoView;
import ru.protei.portal.ui.common.client.activity.info.JiraInfoActivity;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamView;
import ru.protei.portal.ui.common.client.activity.notify.AbstractNotifyView;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.pathitem.PathItemActivity;
import ru.protei.portal.ui.common.client.activity.pathitem.item.AbstractPathItemView;
import ru.protei.portal.ui.common.client.activity.pathitem.list.AbstractPathItemListView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.activity.projectsearch.AbstractProjectSearchView;
import ru.protei.portal.ui.common.client.activity.projectsearch.ProjectSearchActivity;
import ru.protei.portal.ui.common.client.activity.workerposition.edit.AbstractWorkerPositionEditView;
import ru.protei.portal.ui.common.client.activity.workerposition.edit.WorkerPositionEditActivity;
import ru.protei.portal.ui.common.client.activity.ytwork.AbstractYtWorkFilterView;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.service.HomeCompanyService;
import ru.protei.portal.ui.common.client.util.CommentOrHistoryUtils;
import ru.protei.portal.ui.common.client.view.casecomment.item.CaseCommentItemView;
import ru.protei.portal.ui.common.client.view.casecomment.list.CommentAndHistoryListView;
import ru.protei.portal.ui.common.client.view.caselink.item.CaseLinkItemView;
import ru.protei.portal.ui.common.client.view.caselink.list.CaseLinkListView;
import ru.protei.portal.ui.common.client.view.casetag.edit.CaseTagEditView;
import ru.protei.portal.ui.common.client.view.casetag.taglist.CaseTagListView;
import ru.protei.portal.ui.common.client.view.casetag.taglist.item.CaseTagItemView;
import ru.protei.portal.ui.common.client.view.companydepartment.edit.CompanyDepartmentEditView;
import ru.protei.portal.ui.common.client.view.confirmdialog.ConfirmDialogView;
import ru.protei.portal.ui.common.client.view.contactitem.item.ContactItemView;
import ru.protei.portal.ui.common.client.view.contactitem.list.ContactItemListView;
import ru.protei.portal.ui.common.client.view.dialogdetails.DialogDetailsView;
import ru.protei.portal.ui.common.client.view.errorpage.ErrorPageView;
import ru.protei.portal.ui.common.client.view.filter.IssueFilterParamView;
import ru.protei.portal.ui.common.client.view.info.JiraInfoView;
import ru.protei.portal.ui.common.client.view.notify.NotifyView;
import ru.protei.portal.ui.common.client.view.pager.PagerView;
import ru.protei.portal.ui.common.client.view.pathitem.item.PathItemView;
import ru.protei.portal.ui.common.client.view.pathitem.list.PathItemListView;
import ru.protei.portal.ui.common.client.view.projectsearch.ProjectSearchView;
import ru.protei.portal.ui.common.client.view.workerposition.edit.WorkerPositionEditView;
import ru.protei.portal.ui.common.client.view.ytwork.YtWorkFilterView;
import ru.protei.portal.ui.common.client.widget.employeeregstate.EmployeeRegistrationStateModel;
import ru.protei.portal.ui.common.client.widget.issuestate.StateOptionsModel;
import ru.protei.portal.ui.common.client.widget.issuestate.StateSelectorModel;
import ru.protei.portal.ui.common.client.widget.privilege.list.PrivilegeModel;
import ru.protei.portal.ui.common.client.widget.selector.contract.ContractModel;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeModel;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeModel;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionModel;
import ru.protei.portal.ui.common.client.widget.selector.worker.entry.WorkerEntryModel;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.DefaultNotificationHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.web.common.client.activity.section.AbstractSectionItemView;
import ru.protei.winter.web.common.client.view.section.SectionItemView;

/**
 * Описание классов фабрики
 */
public class CommonClientModule extends AbstractGinModule {

    @Override
    protected void configure() {

        requestStaticInjection(CommentOrHistoryUtils.class);

        bind( ExternalLinkActivity.class ).asEagerSingleton();

        //bind( ServerEventBridge.class ).asEagerSingleton();

        bind( ActionBarActivity.class ).asEagerSingleton();
        bind( AbstractSectionItemView.class ).to( SectionItemView.class );

        bind( AbstractDialogDetailsView.class ).to( DialogDetailsView.class );

        bind( ErrorPageActivity.class ).asEagerSingleton();
        bind( AbstractErrorPageView.class ).to(ErrorPageView.class).in( Singleton.class );

        bind( NotifyActivity.class ).asEagerSingleton();
        bind( AbstractNotifyView.class ).to( NotifyView.class );

        bind( ContactItemActivity.class ).asEagerSingleton();
        bind( AbstractContactItemListView.class ).to( ContactItemListView.class );
        bind( AbstractContactItemView.class ).to( ContactItemView.class );

        bind( EmployeeModel.class ).asEagerSingleton();
        bind( StateOptionsModel.class ).asEagerSingleton();
        bind( StateSelectorModel.class ).asEagerSingleton();
        bind( EmployeeRegistrationStateModel.class ).asEagerSingleton();

        bind( HomeCompanyService.class ).asEagerSingleton();

        bind( DateFormatter.class ).in( Singleton.class );

        bind( AbstractPagerView.class ).to( PagerView.class );

        bind( ConfirmDialogActivity.class ).asEagerSingleton();
        bind( AbstractConfirmDialogView.class ).to( ConfirmDialogView.class ).in( Singleton.class );

        bind( PolicyService.class ).asEagerSingleton();
        bind( CaseLinkProvider.class ).asEagerSingleton();

        bind( PathItemActivity.class ).asEagerSingleton();
        bind( AbstractPathItemListView.class ).to( PathItemListView.class );
        bind( AbstractPathItemView.class ).to( PathItemView.class );

        bind( CommentAndHistoryListActivity.class ).asEagerSingleton();
        bind( AbstractCommentAndHistoryListView.class ).to( CommentAndHistoryListView.class ).in( Singleton.class );
        bind( AbstractCaseCommentItemView.class ).to( CaseCommentItemView.class );

        bind( CaseLinkListActivity.class ).asEagerSingleton();
        bind( AbstractCaseLinkItemView.class ).to( CaseLinkItemView.class );
        bind( AbstractCaseLinkListView.class ).to( CaseLinkListView.class ).in( Singleton.class );

        bind( CaseTagSelectorActivity.class ).asEagerSingleton();
        bind( CaseTagListSingletonActivity.class ).asEagerSingleton();
        bind( AbstractCaseTagListActivity.class ).to( CaseTagListActivity.class );
        bind( AbstractCaseTagItemView.class ).to( CaseTagItemView.class );
        bind( AbstractCaseTagListView.class ).to( CaseTagListView.class );
        bind( CaseTagEditActivity.class ).asEagerSingleton();
        bind( CompanyDepartmentEditActivity.class ).asEagerSingleton();
        bind( WorkerPositionEditActivity.class ).asEagerSingleton();
        bind( AbstractCaseTagEditView.class ).to( CaseTagEditView.class ).in( Singleton.class );
        bind( AbstractCompanyDepartmentEditView.class ).to( CompanyDepartmentEditView.class ).in( Singleton.class );
        bind( AbstractWorkerPositionEditView.class ).to( WorkerPositionEditView.class ).in( Singleton.class );

        bind( CustomerTypeModel.class ).asEagerSingleton();

        bind(AbstractJiraInfoActivity.class).to(JiraInfoActivity.class).asEagerSingleton();
        bind(AbstractJiraInfoView.class).to(JiraInfoView.class).in(Singleton.class);

        // Models
        bind( PrivilegeModel.class ).asEagerSingleton();
        bind( ProductDirectionModel.class ).asEagerSingleton();
        bind( WorkerEntryModel.class ).asEagerSingleton();

        bind( ConfigStorage.class ).asEagerSingleton();

        requestStaticInjection(DefaultNotificationHandler.class);
        requestStaticInjection(DefaultErrorHandler.class);
        requestStaticInjection(RequestCallback.class);
        requestStaticInjection(FluentCallback.class);
        requestStaticInjection(DecimalNumberFormatter.class);

        bind( IssueFilterWidgetModel.class ).asEagerSingleton();
        bind( AbstractIssueFilterParamView.class ).to( IssueFilterParamView.class );

        bind(CaseHistoryItemListActivity.class).asEagerSingleton();
        bind(AbstractCaseCommentItemListActivity.class).to(CaseCommentItemListActivity.class).asEagerSingleton();

        bind( ProjectSearchActivity.class ).asEagerSingleton();
        bind(AbstractProjectSearchView.class ).to( ProjectSearchView.class ).in( Singleton.class );

        bind(ContractModel.class).asEagerSingleton();

        bind(AbstractYtWorkFilterView.class).to(YtWorkFilterView.class);
    }
}

