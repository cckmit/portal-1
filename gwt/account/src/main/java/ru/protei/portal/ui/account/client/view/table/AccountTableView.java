package ru.protei.portal.ui.account.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.ui.account.client.activity.table.AbstractAccountTableActivity;
import ru.protei.portal.ui.account.client.activity.table.AbstractAccountTableView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление создания и редактирования учетной записи
 */
public class AccountTableView extends Composite implements AbstractAccountTableView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
    }

    @Override
    public void setActivity( AbstractAccountTableActivity activity ) {
        this.activity = activity;
        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );
        columns.forEach( clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });
        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }

    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public HasWidgets getPreviewContainer () { return previewContainer; }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @Override
    public void hideElements() {
        filterContainer.setVisible( false );
        tableContainer.removeStyleName( "col-xs-9" );
        tableContainer.addStyleName( "col-xs-12" );
    }

    @Override
    public void showElements() {
        filterContainer.setVisible( true );
        tableContainer.removeStyleName( "col-xs-12" );
        tableContainer.addStyleName( "col-xs-9" );
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void setRecordCount( Long count ) {
        table.setTotalRecords( count.intValue() );
    }

    @Override
    public int getPageSize() {
        return table.getPageSize();
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo( int page ) {
        table.scrollToPage( page );
    }

    private void initTable () {

        editClickColumn = new EditClickColumn< UserLogin >( lang ) {};
    }

    @UiField
    InfiniteTableWidget< UserLogin > table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    ClickColumnProvider< UserLogin > columnProvider = new ClickColumnProvider<>();
    SelectionColumn< UserLogin > selectionColumn = new SelectionColumn<>();
    EditClickColumn< UserLogin > editClickColumn;
    List<ClickColumn > columns = new ArrayList<>();


    AbstractAccountTableActivity activity;

    private static AccountTableViewUiBinder ourUiBinder = GWT.create( AccountTableViewUiBinder.class );
    interface AccountTableViewUiBinder extends UiBinder<HTMLPanel, AccountTableView> {}
}