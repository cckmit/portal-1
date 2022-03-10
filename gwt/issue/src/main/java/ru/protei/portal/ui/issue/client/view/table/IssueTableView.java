package ru.protei.portal.ui.issue.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.AttachClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.FavoritesClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.common.client.widget.separator.Separator;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableActivity;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.view.table.columns.ContactColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.ManagerColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.NumberColumn;

import java.util.function.Predicate;


/**
 * Представление таблицы обращений
 */
public class IssueTableView extends Composite implements AbstractIssueTableView {
    @Inject
    public void onInit(EditClickColumn< CaseShortView> editClickColumn) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        initTable();
    }

    @Override
    public void setActivity( AbstractIssueTableActivity activity ) {
        this.activity = activity;

        favoritesClickColumn.setHandler(activity);
        favoritesClickColumn.setColumnProvider(columnProvider);
        favoritesClickColumn.setFavoritesStateManager(activity);

        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        attachClickColumn.setHandler(activity);
        attachClickColumn.setAttachHandler(activity);
        attachClickColumn.setColumnProvider(columnProvider);

        issueNumber.setHandler( activity );
        issueNumber.setColumnProvider( columnProvider );
        contact.setHandler( activity );
        contact.setColumnProvider( columnProvider );
        info.setHandler( activity );
        info.setColumnProvider( columnProvider );
        manager.setHandler( activity );
        manager.setColumnProvider( columnProvider );
        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }
    
    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
        animation.setStyles("col-md-12", "col-md-9", "col-md-3", "col-md-3", "col-md-9");
    }

    @Override
    public HasWidgets getPreviewContainer () { return previewContainer; }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @Override
    public void hideElements() {
        hideContact.setVisibility( false );
        hideManager.setVisibility( false );
    }

    @Override
    public void showElements() {
        hideContact.setVisibility( true );
        hideManager.setVisibility( true );
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo( int page ) {
        table.scrollToPage( page );
    }

    @Override
    public void updateRow(CaseShortView item) {
        if(item != null)
            table.updateRow(item);
    }

    @Override
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        table.setTotalRecords(totalRecords);
    }

    @Override
    public void setChangeSelectionIfSelectedPredicate(Predicate<CaseShortView> changeSelectionIfSelectedPredicate) {
        columnProvider.setChangeSelectionIfSelectedPredicate(changeSelectionIfSelectedPredicate);
    }

    @Override
    public HasVisibility loadingVisibility() {
        return loading;
    }

    private void initTable () {
        attachClickColumn = new AttachClickColumn<CaseShortView>(lang) {};
        attachClickColumn.setDisplayPredicate(caseShortView -> policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) || caseShortView.isPublicAttachmentsExist());
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT) );
        issueNumber = new NumberColumn( lang );
        contact = new ContactColumn( lang );
        manager = new ManagerColumn( lang );
        info = new InfoColumn( lang );
        favoritesClickColumn = new FavoritesClickColumn<>(lang);

        table.addColumn(favoritesClickColumn.header, favoritesClickColumn.values);
        table.addColumn( issueNumber.header, issueNumber.values );
        table.addColumn( info.header, info.values );
        hideContact = table.addColumn( contact.header, contact.values );
        hideManager = table.addColumn( manager.header, manager.values );
        table.addColumn( attachClickColumn.header, attachClickColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
    }

    @UiField
    InfiniteTableWidget<CaseShortView> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel pagerContainer;
    @UiField
    IndeterminateCircleLoading loading;

    @Inject
    PolicyService policyService;
    @Inject
    Separator separator;

    ClickColumnProvider<CaseShortView> columnProvider = new ClickColumnProvider<>();
//    SelectionColumn< CaseShortView > selectionColumn = new SelectionColumn<>();
    EditClickColumn< CaseShortView > editClickColumn;
    AttachClickColumn< CaseShortView > attachClickColumn;
    NumberColumn issueNumber;
    ContactColumn contact;
    ManagerColumn manager;
    InfoColumn info;
    FavoritesClickColumn<CaseShortView> favoritesClickColumn;

    AbstractColumn hideContact;
    AbstractColumn hideManager;

    AbstractIssueTableActivity activity;

    private static IssueTableViewUiBinder ourUiBinder = GWT.create( IssueTableViewUiBinder.class );
    interface IssueTableViewUiBinder extends UiBinder< HTMLPanel, IssueTableView > {}
}
