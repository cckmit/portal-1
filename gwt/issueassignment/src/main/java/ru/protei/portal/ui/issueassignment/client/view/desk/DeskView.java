package ru.protei.portal.ui.issueassignment.client.view.desk;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.issueassignment.client.activity.desk.AbstractDeskActivity;
import ru.protei.portal.ui.issueassignment.client.activity.desk.AbstractDeskView;

public class DeskView extends Composite implements AbstractDeskView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractDeskActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets tableContainer() {
        return table;
    }

    @Override
    public HasVisibility tableViewVisibility() {
        return table;
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
    public HasVisibility notificationVisibility() {
        return notification;
    }

    @Override
    public TakesValue<String> notificationText() {
        return new TakesValue<String>() {
            public void setValue(String value) { notificationMessage.setInnerText(value); }
            public String getValue() { return notificationMessage.getInnerText(); }
        };
    }

    @Override
    public void setFailedViewText(String text) {
        failedViewText.setInnerText(text);
    }

    @UiField
    HTMLPanel table;
    @UiField
    IndeterminateCircleLoading loadingView;
    @UiField
    HTMLPanel failedView;
    @UiField
    HeadingElement failedViewText;
    @UiField
    HTMLPanel notification;
    @UiField
    SpanElement notificationMessage;

    private AbstractDeskActivity activity;

    interface DeskViewBinder extends UiBinder<HTMLPanel, DeskView> {}
    private static DeskViewBinder ourUiBinder = GWT.create(DeskViewBinder.class);
}
