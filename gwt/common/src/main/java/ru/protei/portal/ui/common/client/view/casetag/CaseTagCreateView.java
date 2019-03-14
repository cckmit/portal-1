package ru.protei.portal.ui.common.client.view.casetag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.ui.common.client.activity.casetag.AbstractCaseTagCreateActivity;
import ru.protei.portal.ui.common.client.activity.casetag.AbstractCaseTagCreateView;
import ru.protei.portal.ui.common.client.widget.colorpicker.ColorPicker;

public class CaseTagCreateView extends Composite implements AbstractCaseTagCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractCaseTagCreateActivity activity) {}

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<String> color() {
        return color;
    }

    @UiField
    TextBox name;
    @Inject
    @UiField(provided = true)
    ColorPicker color;

    interface CaseTagCreateViewUiBinder extends UiBinder<Widget, CaseTagCreateView> {}
    private static CaseTagCreateViewUiBinder ourUiBinder = GWT.create(CaseTagCreateViewUiBinder.class);
}
