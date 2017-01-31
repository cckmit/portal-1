package ru.protei.portal.ui.equipment.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.separator.Separator;
import ru.protei.portal.ui.equipment.client.activity.table.AbstractEquipmentTableActivity;
import ru.protei.portal.ui.equipment.client.activity.table.AbstractEquipmentTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление таблицы контактов
 */
public class EquipmentTableView extends Composite implements AbstractEquipmentTableView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
    }

    @Override
    public void setActivity( AbstractEquipmentTableActivity activity ) {
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
        editClickColumn = new EditClickColumn<Equipment>( lang ) {};

        ClickColumn< Equipment > name = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.equipmentName() );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
                Element root = DOM.createDiv();
                root.setInnerHTML( "<b>" + value.getName() + "</b>" + "<br/><i>" + value.getNameBySpecification() + "</i>");
                cell.appendChild( root );
            }
        };
        columns.add( name );

        ClickColumn< Equipment > decimalNumber = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.equipmentDecimalNumber() );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
                Element root = DOM.createDiv();
                cell.appendChild( root );

                if ( value.getPAMR_RegisterNumber() != null ) {
                    Element pamrDecimalNumber = DOM.createDiv();
                    pamrDecimalNumber.setInnerHTML( lang.equipmentOrganizationCodePAMR()
                            + "." + value.getClassifierCode() + "." + value.getPAMR_RegisterNumber() );
                    root.appendChild( pamrDecimalNumber );
                }

                if ( value.getPDRA_RegisterNumber() != null ) {
                    Element pdraDecimalNumber = DOM.createDiv();
                    pdraDecimalNumber.setInnerHTML( lang.equipmentOrganizationCodePDRA()
                            + "." + value.getClassifierCode() + "." + value.getPDRA_RegisterNumber() );
                    root.appendChild( pdraDecimalNumber );
                }
            }
        };
        columns.add( decimalNumber );

        ClickColumn< Equipment > product = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.equipmentProduct() );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
            }
        };
        columns.add( product );


        ClickColumn< Equipment > comment = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.equipmentComment() );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
                if ( value.getComment() == null ) {
                    return;
                }

                cell.setInnerHTML( HTMLHelper.wrapDiv( value.getComment() ) );
            }
        };
        columns.add( comment );

        table.addColumn( name.header, name.values );
        table.addColumn( decimalNumber.header, decimalNumber.values );
        table.addColumn( product.header, product.values );
        table.addColumn( comment.header, comment.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
    }

    @UiField
    InfiniteTableWidget<Equipment> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    Separator separator;


    ClickColumnProvider<Equipment> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<Equipment> editClickColumn;
    List<ClickColumn > columns = new ArrayList<>();


    AbstractEquipmentTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, EquipmentTableView> {}
}