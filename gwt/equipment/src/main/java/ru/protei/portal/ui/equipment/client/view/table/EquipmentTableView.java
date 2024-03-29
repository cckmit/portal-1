package ru.protei.portal.ui.equipment.client.view.table;

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
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.lang.En_EquipmentTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.equipment.client.activity.table.AbstractEquipmentTableActivity;
import ru.protei.portal.ui.equipment.client.activity.table.AbstractEquipmentTableView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Представление таблицы контактов
 */
public class EquipmentTableView extends Composite implements AbstractEquipmentTableView {
    @Inject
    public void onInit(EditClickColumn<Equipment> editClickColumn) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
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
    public void removeSelection (Equipment value){
        columnProvider.removeSelection(value);
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
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo( int page ) {
        table.scrollToPage( page );
    }

    private void initTable () {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.EQUIPMENT_EDIT) );

        ClickColumn< Equipment > name = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.addClassName( "equipment-name-column" );
                element.setInnerText( lang.equipmentNameBySpecification() );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
                cell.addClassName( "equipment-name-column" );
                String nameSldWrksHtml = "<div><small><i class=\"far fa-file m-r-5\"></i>" + value.getNameSldWrks() + "</small></div>";
                cell.setInnerHTML( HTMLHelper.wrapDiv( value.getName() + nameSldWrksHtml ) );
            }
        };
        columns.add( name );

        ClickColumn< Equipment > decimalNumber = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.equipmentDecimalNumber() );
                element.addClassName( "equipment-number-column" );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
                if ( value.getDecimalNumbers() == null ) {
                    return;
                }
                cell.addClassName( "equipment-number-column" );

                Element root = DOM.createDiv();

                for ( DecimalNumber number : value.getDecimalNumbers() ) {
                    Element numElem = DOM.createDiv();
                    numElem.setInnerText( DecimalNumberFormatter.formatNumber( number ) );
                    if ( number.isReserve() ) {
                        Element isReserveEl = DOM.createElement("i");
                        isReserveEl.addClassName( "fa fa-flag text-danger m-l-10" );
                        numElem.appendChild( isReserveEl );
                    }
                    root.appendChild( numElem );
                }
                cell.appendChild( root );
            }
        };
        columns.add( decimalNumber );

        ClickColumn< Equipment > comment = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.addClassName( "equipment-comment column-hidable" );
                element.setInnerText( lang.equipmentComment() );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
                cell.addClassName( "column-hidable" );
                if ( value.getComment() == null ) {
                    return;
                }
                cell.setInnerHTML( "<div><small>" + value.getComment() + "</small></div>" );
            }
        };
        columns.add( comment );

        ClickColumn< Equipment > attachment = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.equipmentAttachment() );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
            }
        };
        columns.add( attachment );

        ClickColumn< Equipment > type = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.addClassName( "equipment-type-column" );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
                Element root = DOM.createDiv();
                cell.addClassName( "equipment-type-column" );
                cell.appendChild( root );
                ImageElement imageElement = DOM.createImg().cast();
                imageElement.setSrc( "./images/eq_" + value.getType().name().toLowerCase() + ".png" );
                imageElement.setTitle( typeLang.getName( value.getType() ) );
                root.appendChild( imageElement );
            }
        };
        columns.add( type );

        ClickColumn< Equipment > primaryUse = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.equipmentPrimaryUse() );
                element.addClassName( "equipment-number-column" );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
                cell.setClassName( "equipment-number-column" );
                Element root = DOM.createDiv();
                if (value != null && value.getLinkedEquipmentDecimalNumbers() != null) {
                    root.setInnerHTML(StringUtils.join(
                            "<div>",
                            value.getLinkedEquipmentDecimalNumbers().stream()
                                    .map(DecimalNumberFormatter::formatNumber).collect(Collectors.joining(", ")),
                            "</div>"
                    ).toString());
                }
                cell.appendChild( root );
            }
        };
        columns.add( primaryUse );

        ClickColumn< Equipment > project = new ClickColumn< Equipment >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.addClassName( "equipment-project column-hidable" );
                element.setInnerText( lang.equipmentProject() );
            }

            @Override
            public void fillColumnValue ( Element cell, Equipment value ) {
                cell.addClassName( "column-hidable" );

                String managerHtml = "";
                if ( value.getManagerShortName() != null ) {
                    managerHtml = "<div><small><i class='far fa-user m-r-5'></i>" + value.getManagerShortName() + "</small></div>";
                }

                cell.setInnerHTML( HTMLHelper.wrapDiv( StringUtils.emptyIfNull(value.getProjectName()) + managerHtml ) );
            }
        };
        columns.add( project );


        table.addColumn( type.header, type.values );
        table.addColumn( name.header, name.values );
        table.addColumn( decimalNumber.header, decimalNumber.values );
        table.addColumn( primaryUse.header, primaryUse.values );
        hideOnShowPreviewProjectColumn = table.addColumn( project.header, project.values );
        hideOnShowPreviewCommentColumn = table.addColumn( comment.header, comment.values );
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
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    @UiField
    Lang lang;
    @Inject
    En_EquipmentTypeLang typeLang;
    @Inject
    PolicyService policyService;

    ClickColumnProvider<Equipment> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<Equipment> editClickColumn;
    List<ClickColumn > columns = new ArrayList<>();
    AbstractColumn hideOnShowPreviewCommentColumn;
    AbstractColumn hideOnShowPreviewProjectColumn;

    AbstractEquipmentTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, EquipmentTableView> {}
}
