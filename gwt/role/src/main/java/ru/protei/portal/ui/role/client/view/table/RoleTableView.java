package ru.protei.portal.ui.role.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.ContactColumnBuilder;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.separator.Separator;
import ru.protei.portal.ui.role.client.activity.table.AbstractRoleTableActivity;
import ru.protei.portal.ui.role.client.activity.table.AbstractRoleTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление таблицы роли
 */
public class RoleTableView extends Composite implements AbstractRoleTableView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
    }

    @Override
    public void setActivity( AbstractRoleTableActivity activity ) {
        this.activity = activity;
        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );
        columns.forEach( clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });
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
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void setData( List<UserRole> roles ) {
        for ( UserRole role : roles ) {
            table.addRow( role );
        }
    }

    private void initTable () {
        editClickColumn = new EditClickColumn<UserRole>( lang ) {};

        ClickColumn< UserRole > name = new ClickColumn< UserRole >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.roleName() );
            }

            @Override
            public void fillColumnValue ( Element cell, UserRole value ) {
                cell.setInnerText( value.getCode() == null ? "" : value.getCode() );
            }
        };
        columns.add( name );

        ClickColumn< UserRole > description = new ClickColumn< UserRole >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.roleDescription() );
            }

            @Override
            public void fillColumnValue ( Element cell, UserRole value ) {
                cell.setInnerText( value.getInfo() == null ? "" : value.getInfo() );
            }
        };
        columns.add( description );

        table.addColumn( name.header, name.values );
        table.addColumn( description.header, description.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
    }

    @UiField
    TableWidget<UserRole> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    ClickColumnProvider<UserRole> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<UserRole > editClickColumn;
    List<ClickColumn > columns = new ArrayList<>();

    AbstractRoleTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, RoleTableView > {}
}