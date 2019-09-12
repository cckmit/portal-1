package ru.protei.portal.ui.common.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.activity.actionbar.ActionBarActivity;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentView;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;
import ru.protei.portal.ui.common.client.activity.casecomment.list.AbstractCaseCommentListView;
import ru.protei.portal.ui.common.client.activity.casecomment.list.CaseCommentListActivity;
import ru.protei.portal.ui.common.client.activity.caselinkprovider.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.casetag.AbstractCaseTagEditView;
import ru.protei.portal.ui.common.client.activity.casetag.CaseTagEditActivity;
import ru.protei.portal.ui.common.client.activity.confirmdialog.AbstractConfirmDialogView;
import ru.protei.portal.ui.common.client.activity.confirmdialog.ConfirmDialogActivity;
import ru.protei.portal.ui.common.client.activity.contactitem.AbstractContactItemListView;
import ru.protei.portal.ui.common.client.activity.contactitem.AbstractContactItemView;
import ru.protei.portal.ui.common.client.activity.contactitem.ContactItemActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.activity.notify.AbstractNotifyView;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.pathitem.PathItemActivity;
import ru.protei.portal.ui.common.client.activity.pathitem.item.AbstractPathItemView;
import ru.protei.portal.ui.common.client.activity.pathitem.list.AbstractPathItemListView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.common.IssueStates;
import ru.protei.portal.ui.common.client.view.attachment.AttachmentView;
import ru.protei.portal.ui.common.client.view.casecomment.item.CaseCommentItemView;
import ru.protei.portal.ui.common.client.view.casecomment.list.CaseCommentListView;
import ru.protei.portal.ui.common.client.view.casetag.CaseTagEditView;
import ru.protei.portal.ui.common.client.view.confirmdialog.ConfirmDialogView;
import ru.protei.portal.ui.common.client.view.contactitem.item.ContactItemView;
import ru.protei.portal.ui.common.client.view.contactitem.list.ContactItemListView;
import ru.protei.portal.ui.common.client.view.dialogdetails.DialogDetailsView;
import ru.protei.portal.ui.common.client.view.notify.NotifyView;
import ru.protei.portal.ui.common.client.view.pager.PagerView;
import ru.protei.portal.ui.common.client.view.pathitem.item.PathItemView;
import ru.protei.portal.ui.common.client.view.pathitem.list.PathItemListView;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyModel;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterParamView;
import ru.protei.portal.ui.common.client.widget.issuestate.StateModel;
import ru.protei.portal.ui.common.client.widget.privilege.list.PrivilegeModel;
import ru.protei.portal.ui.common.client.widget.selector.casetag.CaseTagModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeModel;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeModel;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionModel;
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
        bind( ActionBarActivity.class ).asEagerSingleton();
        bind( AbstractSectionItemView.class ).to( SectionItemView.class );

        bind( AbstractDialogDetailsView.class ).to( DialogDetailsView.class );

        bind( NotifyActivity.class ).asEagerSingleton();
        bind( AbstractNotifyView.class ).to( NotifyView.class );

        bind( ContactItemActivity.class ).asEagerSingleton();
        bind( AbstractContactItemListView.class ).to( ContactItemListView.class );
        bind( AbstractContactItemView.class ).to( ContactItemView.class );

        bind( CompanyModel.class ).asEagerSingleton();
        bind( HomeCompanyModel.class ).asEagerSingleton();
        bind( EmployeeModel.class ).asEagerSingleton();
        bind( StateModel.class ).asEagerSingleton();

        bind( DateFormatter.class ).in( Singleton.class );

        bind( AbstractPagerView.class ).to( PagerView.class );

        bind( IssueStates.class ).asEagerSingleton();
        bind( AbstractAttachmentView.class ).to( AttachmentView.class );

        bind( ConfirmDialogActivity.class ).asEagerSingleton();
        bind( AbstractConfirmDialogView.class ).to( ConfirmDialogView.class ).in( Singleton.class );

        bind( PolicyService.class ).asEagerSingleton();
        bind( CaseLinkProvider.class ).asEagerSingleton();

        bind( PathItemActivity.class ).asEagerSingleton();
        bind( AbstractPathItemListView.class ).to( PathItemListView.class );
        bind( AbstractPathItemView.class ).to( PathItemView.class );

        bind( CaseCommentListActivity.class ).asEagerSingleton();
        bind( AbstractCaseCommentListView.class ).to( CaseCommentListView.class ).in( Singleton.class );
        bind( AbstractCaseCommentItemView.class ).to( CaseCommentItemView.class );

        bind( CaseTagEditActivity.class ).asEagerSingleton();
        bind( AbstractCaseTagEditView.class ).to( CaseTagEditView.class ).in( Singleton.class );

        // Models
        bind( InitiatorModel.class ).asEagerSingleton();
        bind( PrivilegeModel.class ).asEagerSingleton();
        bind( ProductModel.class ).asEagerSingleton();
        bind( ProductDirectionModel.class ).asEagerSingleton();
        bind( CaseTagModel.class ).asEagerSingleton();

        requestStaticInjection(DefaultNotificationHandler.class);
        requestStaticInjection(DefaultErrorHandler.class);
        requestStaticInjection(RequestCallback.class);
        requestStaticInjection(FluentCallback.class);
        requestStaticInjection(DecimalNumberFormatter.class);

        bind( AbstractIssueFilterWidgetView.class ).to( IssueFilterParamView.class ).in( Singleton.class );
        bind( CustomerTypeModel.class ).asEagerSingleton();
    }
}

