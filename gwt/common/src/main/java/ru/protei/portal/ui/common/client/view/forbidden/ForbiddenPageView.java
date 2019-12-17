package ru.protei.portal.ui.common.client.view.forbidden;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.forbidden.AbstractForbiddenPageView;
import ru.protei.portal.ui.common.client.lang.Lang;

public class ForbiddenPageView extends Composite implements AbstractForbiddenPageView {
    public ForbiddenPageView() {
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

    interface ForbiddenPageViewUiBinder extends UiBinder<HTMLPanel, ForbiddenPageView> {
    }
    private static ForbiddenPageViewUiBinder ourUiBinder = GWT.create(ForbiddenPageViewUiBinder.class);
}