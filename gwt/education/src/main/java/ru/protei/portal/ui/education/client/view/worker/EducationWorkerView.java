package ru.protei.portal.ui.education.client.view.worker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.education.client.activity.worker.AbstractEducationWorkerActivity;
import ru.protei.portal.ui.education.client.activity.worker.AbstractEducationWorkerView;

public class EducationWorkerView extends Composite implements AbstractEducationWorkerView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEducationWorkerActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets walletContainer() {
        return walletList;
    }

    @Override
    public HasWidgets tableContainer() {
        return tableContainer;
    }

    @Override
    public HasVisibility walletLoadingViewVisibility() {
        return walletLoadingView;
    }

    @Override
    public HasVisibility walletFailedViewVisibility() {
        return walletFailedView;
    }

    @Override
    public void walletFailedViewText(String text) {
        walletFailedViewText.setInnerText(text);
    }

    @UiField
    PlateList walletList;
    @UiField
    IndeterminateCircleLoading walletLoadingView;
    @UiField
    HTMLPanel walletFailedView;
    @UiField
    HeadingElement walletFailedViewText;
    @UiField
    HTMLPanel tableContainer;

    private AbstractEducationWorkerActivity activity;

    interface EducationWalletViewBinder extends UiBinder<HTMLPanel, EducationWorkerView> {}
    private static EducationWalletViewBinder ourUiBinder = GWT.create(EducationWalletViewBinder.class);
}
