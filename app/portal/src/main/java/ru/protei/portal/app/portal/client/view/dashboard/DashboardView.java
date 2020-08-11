package ru.protei.portal.app.portal.client.view.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.activity.dashboard.AbstractDashboardView;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.common.client.widget.quickview.QuickView;

public class DashboardView extends Composite implements AbstractDashboardView{

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void addTableToContainer (Widget widget){
        if (containerRight.getWidgetCount() < containerLeft.getWidgetCount()) {
            containerRight.add(widget);
        } else {
            containerLeft.add(widget);
        }
    }

    @Override
    public void clearContainers(){
        containerLeft.clear();
        containerRight.clear();
    }

    @Override
    public HasVisibility loadingViewVisibility() {
        return loadingView;
    }

    @Override
    public HasVisibility failedViewVisibility() {
        return failedView;
    }

    @Override
    public HasVisibility emptyViewVisibility() {
        return emptyView;
    }

    @Override
    public HasWidgets quickview() {
        return quickview;
    }

    @Override
    public void showQuickview(boolean isShow) {
        quickview.show(isShow);
    }

    @Override
    public void setFailedViewText(String text) {
        failedViewText.setInnerText(text);
    }

    @UiField
    HTMLPanel containerLeft;
    @UiField
    HTMLPanel containerRight;
    @UiField
    QuickView quickview;
    @UiField
    IndeterminateCircleLoading loadingView;
    @UiField
    HTMLPanel failedView;
    @UiField
    HeadingElement failedViewText;
    @UiField
    HTMLPanel emptyView;

    interface DashboardViewUiBinder extends UiBinder<HTMLPanel, DashboardView> {}
    private static DashboardViewUiBinder ourUiBinder = GWT.create(DashboardViewUiBinder.class);
}