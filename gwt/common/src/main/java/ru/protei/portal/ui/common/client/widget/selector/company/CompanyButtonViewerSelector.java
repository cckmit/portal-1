package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

public class CompanyButtonViewerSelector extends Selector<EntityOption> implements SelectorWithModel<EntityOption> {

    public CompanyButtonViewerSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Inject
    public void init() {
        setSearchEnabled(true);
        setSearchAutoFocus(true);
        setDisplayOptionCreator(value -> {
            if (value == null) {
                return new DisplayOption(defaultValue);
            }
            return new DisplayOption(value.getDisplayText());
        });
    }

    @Override
    public void fillSelectorView(DisplayOption selectedValue) {}

    @Override
    public void fillOptions(List<EntityOption> companies) {
        clearOptions();
        addOption(null);
        if (companies != null) {
            companies.forEach(this::addOption);
        }
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    @UiHandler("button")
    public void onBtnClick (ClickEvent event) {
        showPopupInlineRight(button);
    }

    @UiField
    HTMLPanel inputContainer;
    @UiField
    Button button;

    private String defaultValue = "";

    interface CompanyButtonViewerSelectorUiBinder extends UiBinder<HTMLPanel, CompanyButtonViewerSelector> {}
    private static CompanyButtonViewerSelectorUiBinder ourUiBinder = GWT.create(CompanyButtonViewerSelectorUiBinder.class);
}
