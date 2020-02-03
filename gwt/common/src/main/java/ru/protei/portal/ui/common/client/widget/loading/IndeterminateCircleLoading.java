package ru.protei.portal.ui.common.client.widget.loading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;

public class IndeterminateCircleLoading extends Composite implements HasVisibility {

    public IndeterminateCircleLoading() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    interface IndeterminateCircleLoadingUiBinder extends UiBinder<HTMLPanel, IndeterminateCircleLoading> {}
    private static IndeterminateCircleLoadingUiBinder ourUiBinder = GWT.create(IndeterminateCircleLoadingUiBinder.class);
}
