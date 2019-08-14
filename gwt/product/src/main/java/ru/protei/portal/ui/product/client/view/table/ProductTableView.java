package ru.protei.portal.ui.product.client.view.table;

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
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductTableActivity;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductTableView;

public class ProductTableView extends Composite implements AbstractProductTableView{

    @Inject
    public void onInit(EditClickColumn<DevUnit> editClickColumn, ArchiveClickColumn<DevUnit> archiveClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.archiveClickColumn = archiveClickColumn;

        editClickColumn.setArchivedCheckFunction(DevUnit::isDeprecatedUnit);
        archiveClickColumn.setArchivedCheckFunction(DevUnit::isDeprecatedUnit);
        initTable();
    }

    @Override
    public void setActivity(AbstractProductTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        archiveClickColumn.setArchiveHandler(activity);
        archiveClickColumn.setColumnProvider(columnProvider);

        name.setHandler( activity );
        name.setColumnProvider( columnProvider );
        type.setHandler( activity );
        type.setColumnProvider( columnProvider );
        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public HasWidgets getFilterContainer() {
        return filterContainer;
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
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
    public void scrollTo(int page) {
        table.scrollToPage( page );
    }

    @Override
    public void updateRow(DevUnit item) {
        if(item != null)
            table.updateRow(item);
    }

    private void initTable () {
        editClickColumn.setPrivilege( En_Privilege.COMPANY_EDIT );
        archiveClickColumn.setPrivilege( En_Privilege.COMPANY_EDIT );
        
        name = new DynamicColumn<>(lang.productName(), "product-name", devUnit -> {
            StringBuilder stringBuilder = new StringBuilder();

            if (devUnit.isActiveUnit()) {
                stringBuilder.append(devUnit.getName());
            } else {
                stringBuilder
                        .append("<div class=\"deprecated-entity\">")
                        .append("<i class=\"fa fa-lock m-r-5\" id=\"" + DebugIds.DEBUG_ID_PREFIX + DebugIds.PRODUCT_TABLE.LOCK_ICON + "\"></i> ")
                        .append(devUnit.getName())
                        .append("</div>");
            }

            return stringBuilder.toString();
        });
        type = new ClickColumn<DevUnit>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.addClassName("dev-unit-type-column");
            }

            @Override
            public void fillColumnValue(Element cell, DevUnit value) {
                Element root = DOM.createDiv();
                root.addClassName("dev-unit-type-column");
                cell.appendChild(root);
                ImageElement imageElement = DOM.createImg().cast();
                imageElement.setSrc(value.getType().getImgSrc());
                imageElement.setTitle(typeLang.getName(value.getType()));
                imageElement.setAlt(typeLang.getName(value.getType()));
                root.appendChild(imageElement);
            }
        };

        table.addColumn( type.header, type.values );
        table.addColumn( name.header, name.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn(archiveClickColumn.header, archiveClickColumn.values);
    }

    @UiField
    InfiniteTableWidget<DevUnit> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    Lang lang;
    @Inject
    En_DevUnitTypeLang typeLang;

    ClickColumnProvider< DevUnit > columnProvider = new ClickColumnProvider<>();
    EditClickColumn< DevUnit > editClickColumn;
    ArchiveClickColumn<DevUnit> archiveClickColumn;
    DynamicColumn<DevUnit> name;
    ClickColumn<DevUnit> type;

    AbstractProductTableActivity activity;

    private static ProductTableViewUiBinder ourUiBinder = GWT.create(ProductTableViewUiBinder.class);
    interface ProductTableViewUiBinder extends UiBinder<HTMLPanel, ProductTableView> {}
}