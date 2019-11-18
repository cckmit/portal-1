package ru.protei.portal.ui.common.client.view.forbidden;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.forbidden.AbstractForbiddenPageView;

public class ForbiddenPageView extends Composite implements AbstractForbiddenPageView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        this.errorMsg.setInnerText(errorMsg);
    }

    @UiField
    HTMLPanel panel;

    @UiField
    HeadingElement errorMsg;

    interface ForbiddenPageViewUiBinder extends UiBinder<HTMLPanel, ForbiddenPageView> {
    }
    private static ForbiddenPageViewUiBinder ourUiBinder = GWT.create(ForbiddenPageViewUiBinder.class);
}