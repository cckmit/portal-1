package ru.protei.portal.ui.common.client.view.deliveryfilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryCollapseFilterActivity;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryCollapseFilterView;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Представление фильтра поставок
 */
public class DeliveryFilterCollapseView extends Composite implements AbstractDeliveryCollapseFilterView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDeliveryCollapseFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getContainer () {
        return container;
    }

    @UiHandler("filterRestoreBtn")
    public void onFilterRestoreBtnClick(ClickEvent event) {
        if (activity != null) {
            activity.onFilterRestore();
        }
    }

    @UiHandler("filterCollapseBtn")
    public void onFilterCollapseBtnClick(ClickEvent event) {
        if (activity != null) {
            activity.onFilterCollapse();
        }
    }

    private void ensureDebugIds() {
        labelFilters.setId(DebugIds.FILTER.FILTERS_LABEL);
        filterCollapseBtn.ensureDebugId(DebugIds.FILTER.COLLAPSE_BUTTON);
        filterRestoreBtn.ensureDebugId(DebugIds.FILTER.RESTORE_BUTTON);
    }

    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;
    @UiField()
    HTMLPanel container;
    @UiField
    Anchor filterRestoreBtn;
    @UiField
    Anchor filterCollapseBtn;
    @UiField
    LabelElement labelFilters;

    private AbstractDeliveryCollapseFilterActivity activity;

    private static DeliveryFilterViewUiBinder ourUiBinder = GWT.create( DeliveryFilterViewUiBinder.class );
    interface DeliveryFilterViewUiBinder extends UiBinder<HTMLPanel, DeliveryFilterCollapseView> {}
}