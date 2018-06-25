package ru.protei.portal.ui.common.client.widget.issuelinks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.ui.common.client.widget.issuelinks.link.IssueLink;
import ru.protei.portal.ui.common.client.widget.issuelinks.popup.CreateLinkPopup;

import java.util.*;

public class IssueLinks extends Composite implements HasValue<Set<CaseLink>> {

    public IssueLinks() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public Set<CaseLink> getValue() {
        return items;
    }

    @Override
    public void setValue(Set<CaseLink> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<CaseLink> value, boolean fireEvents) {
        items = value;

        itemToViewModel.clear();
        linksContainer.clear();

        toggleVisibility();
        if (items == null || items.size() == 0) {
            return;
        }

        for (CaseLink item : items) {
            IssueLink itemView = itemViewProvider.get();
            itemView.setValue(item);
            itemView.addCloseHandler(event -> removeValue(event.getTarget()));
            itemToViewModel.put(item, itemView);
            linksContainer.add(itemView);
        }

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<CaseLink>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("addLinkButton")
    public void addLinkButtonClick(ClickEvent event) {
        showPopup();
    }

    private void showPopup() {
        popup.showNear(addLinkButton);
        popup.addValueChangeHandler(event -> addValue(event.getValue()));
    }

    private void toggleVisibility() {
        if (items == null || items.size() == 0) {
            linksLabel.addClassName("hide");
            linksContainer.addStyleName("hide");
        } else {
            linksLabel.removeClassName("hide");
            linksContainer.removeStyleName("hide");
        }
    }

    private void addValue(CaseLink item) {
        if (item == null) {
            return;
        }
        Set<CaseLink> value = getValue();
        if (value == null) {
            value = new HashSet<>();
        }
        for (CaseLink cl : value) {
            if (
                    cl.getRemoteId().equals(item.getRemoteId()) &&
                    cl.getType().equals(item.getType())
            ) {
                return;
            }
        }
        value.add(item);
        setValue(value);
    }

    private void removeValue(CaseLink item) {
        if (item == null) {
            return;
        }

        items.remove(item);
        IssueLink itemView = itemToViewModel.get(item);
        if (itemView != null) {
            linksContainer.remove(itemView);
        }

        toggleVisibility();
    }

    private Map<CaseLink, IssueLink> itemToViewModel = new HashMap<>();
    private Set<CaseLink> items = null;

    @Inject
    Provider<IssueLink> itemViewProvider;
    @Inject
    CreateLinkPopup popup;

    @UiField
    LabelElement linksLabel;
    @UiField
    HTMLPanel linksContainer;
    @UiField
    Button addLinkButton;

    interface IssueLinksViewUiBinder extends UiBinder<HTMLPanel, IssueLinks> {}
    private static IssueLinksViewUiBinder ourUiBinder = GWT.create(IssueLinksViewUiBinder.class);
}
