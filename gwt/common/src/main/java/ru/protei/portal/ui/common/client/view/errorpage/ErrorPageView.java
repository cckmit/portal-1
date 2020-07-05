package ru.protei.portal.ui.common.client.view.errorpage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import ru.protei.portal.ui.common.client.activity.errorpage.AbstractErrorPageView;

public class ErrorPageView extends Composite implements AbstractErrorPageView {
    public ErrorPageView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public HasText label() {
        return label;
    }

    @UiField
    HTMLPanel panel;

    @UiField
    Label label;

    interface ErrorPageViewUiBinder extends UiBinder<HTMLPanel, ErrorPageView> {
    }
    private static ErrorPageViewUiBinder ourUiBinder = GWT.create(ErrorPageViewUiBinder.class);
}
