package ru.protei.portal.ui.product.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.product.client.activity.table.AbstractProductTableActivity;
import ru.protei.portal.ui.product.client.activity.table.AbstractProductTableView;

public class ProductTableView extends Composite implements AbstractProductTableView {

    @Inject
    public void onInit(EditClickColumn<DevUnit> editClickColumn, ArchiveClickColumn<DevUnit> archiveClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.archiveClickColumn = archiveClickColumn;
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
        columnProvider.setChangeSelectionIfSelectedPredicate(product -> animation.isPreviewShow());
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

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    private void initTable () {

        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.PRODUCT_EDIT) && !v.isDeprecatedUnit());
        archiveClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.PRODUCT_EDIT));
        archiveClickColumn.setArchiveFilter(DevUnit::isDeprecatedUnit);

        name = new DynamicColumn<>(lang.name(), "product-name", devUnit -> {
            StringBuilder builder = new StringBuilder();
            builder.append(devUnit.isActiveUnit() ? "<div>" : "<div class='deprecated-entity'><i class='fa fa-lock m-r-5'></i> ")
                    .append(SimpleHtmlSanitizer.sanitizeHtml(devUnit.getName()).asString())
                    .append(CollectionUtils.isEmpty(devUnit.getAliases()) ? "" :
                            " (" + String.join(", ", devUnit.getAliases()) + ")")
                    .append("</div>");
            if (StringUtils.isNotEmpty(devUnit.getInfo())) {
                builder.append("<small><i>")
                        .append(devUnit.getInfo())
                        .append("</i></small>");
            }

            return builder.toString();
        });

        type = new DynamicColumn<>(null, "column-img", value -> "<img src='" + value.getType().getImgSrc() + "' title='" + typeLang.getName(value.getType()) + "'></img>");

        table.addColumn( type.header, type.values );
        table.addColumn( name.header, name.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( archiveClickColumn.header, archiveClickColumn.values );
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
    @Inject
    PolicyService policyService;

    ClickColumnProvider< DevUnit > columnProvider = new ClickColumnProvider<>();
    EditClickColumn< DevUnit > editClickColumn;
    ArchiveClickColumn<DevUnit> archiveClickColumn;
    DynamicColumn<DevUnit> name;
    ClickColumn<DevUnit> type;

    AbstractProductTableActivity activity;

    private static ProductTableViewUiBinder ourUiBinder = GWT.create(ProductTableViewUiBinder.class);
    interface ProductTableViewUiBinder extends UiBinder<HTMLPanel, ProductTableView> {}
}
