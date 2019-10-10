package ru.protei.portal.ui.account.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.ui.account.client.activity.table.AbstractAccountTableActivity;
import ru.protei.portal.ui.account.client.activity.table.AbstractAccountTableView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление создания и редактирования учетной записи
 */
public class AccountTableView extends Composite implements AbstractAccountTableView {

    @Inject
    public void onInit(EditClickColumn< UserLogin > editClickColumn,
                       RemoveClickColumn< UserLogin > removeClickColumn) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();

    }

    @Override
    public void setActivity( AbstractAccountTableActivity activity ) {
        this.activity = activity;

        columns.forEach( clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });

        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        removeClickColumn.setHandler( activity );
        removeClickColumn.setRemoveHandler( activity );
        removeClickColumn.setColumnProvider( columnProvider );
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
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void addRecords( List< UserLogin > accounts ) {
        accounts.forEach( userLogin -> table.addRow( userLogin ) );
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void clearSelection() {
        columnProvider.setSelectedValue(null);
    }

    private void initTable () {

        ClickColumn< UserLogin > type = new ClickColumn< UserLogin >() {
            @Override
            protected void fillColumnHeader( Element columnHeader ) {
                columnHeader.addClassName( "type" );
                columnHeader.setInnerText( lang.accountType() );
            }

            @Override
            public void fillColumnValue( Element cell, UserLogin value ) {
                cell.addClassName( "type " + En_AdminState.find( value.getAdminStateId() ).toString().toLowerCase() );

                Element root = DOM.createDiv();
                cell.appendChild( root );

                ImageElement imageElement = DOM.createImg().cast();
                imageElement.setSrc( "./images/auth_" + En_AuthType.find( value.getAuthTypeId() ).toString().toLowerCase() + ".png" );
                imageElement.setTitle( value.getAuthTypeId() == En_AuthType.LDAP.getId() ? lang.accountLDAP() : lang.accountLocal() );
                root.appendChild( imageElement );
            }
        };

        ClickColumn< UserLogin > login = new ClickColumn< UserLogin >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.accountLogin() );
                element.addClassName( "login" );
            }

            @Override
            public void fillColumnValue ( Element cell, UserLogin value ) {
                cell.addClassName( "login " + En_AdminState.find( value.getAdminStateId() ).toString().toLowerCase() );

                Element root = DOM.createDiv();
                cell.appendChild( root );

                Element loginElement = DOM.createDiv();
                loginElement.setInnerHTML( "<b>" + value.getUlogin() + "<b>" );
                root.appendChild( loginElement );
            }
        };

        ClickColumn< UserLogin > person = new ClickColumn< UserLogin >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.accountPerson() );
                element.addClassName( "person" );
            }

            @Override
            public void fillColumnValue ( Element cell, UserLogin value ) {
                cell.addClassName( "person " + En_AdminState.find( value.getAdminStateId() ).toString().toLowerCase() );

                Element root = DOM.createDiv();
                cell.appendChild( root );

                Element personElement = DOM.createDiv();
                personElement.setInnerText( value.getDisplayName() );
                root.appendChild( personElement );

                if ( value.getCompanyName() == null ) {
                    return;
                }

                Element companyElement = DOM.createDiv();
                root.appendChild( companyElement );
                companyElement.setInnerHTML( "<i>" + value.getCompanyName() + "</i>");
            }
        };

        editClickColumn.setPrivilege( En_Privilege.ACCOUNT_EDIT );
        removeClickColumn.setPrivilege( En_Privilege.ACCOUNT_REMOVE );

        columns.add( type );
        columns.add( login );
        columns.add( person );

        //table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( type.header, type.values );
        table.addColumn( login.header, login.values );
        table.addColumn( person.header, person.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( removeClickColumn.header, removeClickColumn.values );
        //selectionColumn.setWidthColumn( 10, Style.Unit.PX );
    }

    @UiField
    TableWidget< UserLogin > table;

    @UiField
    HTMLPanel tableContainer;

    @UiField
    HTMLPanel previewContainer;

    @UiField
    HTMLPanel filterContainer;

    @UiField
    HTMLPanel pagerContainer;

    @Inject
    @UiField
    Lang lang;

    ClickColumnProvider< UserLogin > columnProvider = new ClickColumnProvider<>();
    SelectionColumn< UserLogin > selectionColumn = new SelectionColumn<>();
    EditClickColumn< UserLogin > editClickColumn;
    RemoveClickColumn< UserLogin > removeClickColumn;
    List< ClickColumn > columns = new ArrayList<>();

    AbstractAccountTableActivity activity;

    private static AccountTableViewUiBinder ourUiBinder = GWT.create( AccountTableViewUiBinder.class );
    interface AccountTableViewUiBinder extends UiBinder< HTMLPanel, AccountTableView > {}
}