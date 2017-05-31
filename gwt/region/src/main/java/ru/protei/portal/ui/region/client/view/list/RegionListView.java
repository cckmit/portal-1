package ru.protei.portal.ui.region.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.region.client.activity.list.AbstractRegionListActivity;
import ru.protei.portal.ui.region.client.activity.list.AbstractRegionListView;

/**
 * Вид списка регионов
 */
public class RegionListView extends Composite implements AbstractRegionListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setActivity(AbstractRegionListActivity activity) { this.activity = activity;  }

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @UiField
    PlateList childContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    AbstractRegionListActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, RegionListView > {}

}