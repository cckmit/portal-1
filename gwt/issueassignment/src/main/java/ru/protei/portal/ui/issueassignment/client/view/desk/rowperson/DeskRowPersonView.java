package ru.protei.portal.ui.issueassignment.client.view.desk.rowperson;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.stringselectpopup.StringSelectPopup;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowperson.AbstractDeskRowPersonView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeskRowPersonView extends Composite implements AbstractDeskRowPersonView {

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
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void setPeople(List<PersonShortView> people, int issuesCount) {
        String value = CollectionUtils.stream(people)
                .map(PersonShortView::getName)
                .collect(Collectors.joining(", "));
        value += " (" + issuesCount + ")";
        label.setInnerText(value);
    }

    @Override
    public void setIconExpanded(boolean isExpanded) {
        if (isExpanded) {
            expandButtonIcon.setClassName("fas fa-angle-down");
        } else {
            expandButtonIcon.setClassName("fas fa-angle-right");
        }
    }

    @UiHandler("optionsButton")
    public void optionsButtonClick(ClickEvent event) {
        showActionsPopup();
    }

    @UiHandler("expandButton")
    public void expandButtonClick(ClickEvent event) {
        if (handler != null) {
            handler.onToggleIssuesVisibility();
        }
    }

    private void showActionsPopup() {
        if (CollectionUtils.isEmpty(actions)) {
            return;
        }
        popup.setValues(actions);
        popup.showUnderLeft(optionsButton, null);
    }

    @Inject
    Lang lang;

    @UiField
    SpanElement label;
    @UiField
    Button optionsButton;
    @UiField
    Button expandButton;
    @UiField
    Element expandButtonIcon;

    private List<String> actions = new ArrayList<>();
    private StringSelectPopup popup = new StringSelectPopup();
    private Handler handler;

    interface DeskRowPersonViewBinder extends UiBinder<HTMLPanel, DeskRowPersonView> {}
    private static DeskRowPersonViewBinder ourUiBinder = GWT.create(DeskRowPersonViewBinder.class);
}
