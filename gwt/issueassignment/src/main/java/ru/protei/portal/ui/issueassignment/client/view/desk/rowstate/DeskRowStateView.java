package ru.protei.portal.ui.issueassignment.client.view.desk.rowstate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.stringselectpopup.StringSelectPopup;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowstate.AbstractDeskRowStateView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeskRowStateView extends Composite implements AbstractDeskRowStateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        actions.clear();
        actions.add(lang.issueAssignmentEntryEdit());
        actions.add(lang.issueAssignmentEntryRemove());
        popup.addValueChangeHandler(event -> {
            if (handler == null) {
                return;
            }
            if (lang.issueAssignmentEntryEdit().equals(event.getValue())) {
                handler.onEdit();
                return;
            }
            if (lang.issueAssignmentEntryRemove().equals(event.getValue())) {
                handler.onRemove();
                return;
            }
        });
    }

    @Override
    public UIObject rootContainer() {
        return root;
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void setStates(List<EntityOption> states, int issuesCount) {
        String value = CollectionUtils.stream(states)
                .map(EntityOption::getDisplayText)
                .collect(Collectors.joining(", "));
        value += " (" + issuesCount + ")";
        label.setInnerText(value);
    }

    @UiHandler("optionsButton")
    public void optionsButtonClick(ClickEvent event) {
        showActionsPopup();
    }

    private void showActionsPopup() {
        if (CollectionUtils.isEmpty(actions)) {
            return;
        }
        popup.setValues(actions);
        popup.showUnderRight(optionsButton, 120);
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    SpanElement label;
    @UiField
    HTMLPanel root;
    @UiField
    Button optionsButton;

    private List<String> actions = new ArrayList<>();
    private StringSelectPopup popup = new StringSelectPopup();
    private Handler handler;

    interface DeskRowStateViewBinder extends UiBinder<HTMLPanel, DeskRowStateView> {}
    private static DeskRowStateViewBinder ourUiBinder = GWT.create(DeskRowStateViewBinder.class);
}
