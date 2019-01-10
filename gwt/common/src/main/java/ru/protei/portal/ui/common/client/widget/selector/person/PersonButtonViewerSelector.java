package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

import java.util.List;

public class PersonButtonViewerSelector extends Selector<PersonShortView> implements SelectorWithModel<PersonShortView> {

    public PersonButtonViewerSelector() {
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
            return new DisplayOption(
                    value.getDisplayShortName(),
                    value.isFired() ? "not-active" : "",
                    value.isFired() ? "fa fa-ban ban" : ""
            );
        });
    }

    @Override
    public void fillSelectorView(DisplayOption selectedValue) {}

    @Override
    public void fillOptions(List<PersonShortView> persons) {
        clearOptions();
        addOption(null);
        if (persons != null) {
            persons.forEach(this::addOption);
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

    interface PersonButtonViewerSelectorUiBinder extends UiBinder<HTMLPanel, PersonButtonViewerSelector> {}
    private static PersonButtonViewerSelectorUiBinder ourUiBinder = GWT.create(PersonButtonViewerSelectorUiBinder.class);
}
