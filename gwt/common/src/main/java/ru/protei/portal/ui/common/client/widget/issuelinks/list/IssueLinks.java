package ru.protei.portal.ui.common.client.widget.issuelinks.list;

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
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.activity.caselinkprovider.CaseLinkProvider;
import ru.protei.portal.ui.common.client.widget.issuelinks.link.IssueLink;
import ru.protei.portal.ui.common.client.widget.issuelinks.popup.CreateLinkPopup;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;

public class IssueLinks extends Composite implements HasValue<Set<CaseLink>>, HasEnabled {


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

        items.forEach(this::makeItemAndAddToParent);
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

    @Override
    public boolean isEnabled() {
        return addLinkButton.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        addLinkButton.setVisible(enabled);
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

        if (isCrmLink(item)) {
            Long crmRemoteId;
            try {
                crmRemoteId = Long.parseLong(item.getRemoteId());
            } catch (NumberFormatException ex) {
                // показать ошибку
                return;
            }

            caseLinkProvider.checkExistCrmLink(crmRemoteId, new RequestCallback<CaseInfo>() {
                @Override
                public void onError(Throwable throwable) {}

                @Override
                public void onSuccess(CaseInfo caseInfo) {
                    if ( caseInfo == null ) {
                        // показать ошибку
                        return;
                    }

                    item.setRemoteId(caseInfo.getId().toString());
                    item.setCaseInfo(caseInfo);
                    item.setLink(caseLinkProvider.getLink(item.getType(), crmRemoteId.toString()));

                    if (items == null) {
                        items = new HashSet<>();
                    }

                    if ( items.stream()
                            .anyMatch(cl -> cl.getRemoteId().equals(item.getRemoteId())
                                    && cl.getType().equals(item.getType()))) {
                        return;
                    }
                    items.add(item);
                    makeItemAndAddToParent(item);

                    ValueChangeEvent.fire(IssueLinks.this, items );
                }
            });
        }
    }

    private boolean isCrmLink(CaseLink item) {
        return En_CaseLink.CRM.equals(item.getType());
    }

    private void makeItemAndAddToParent(CaseLink item) {
        IssueLink itemView = itemViewProvider.get();
        itemView.setEnabled(enabled);
        itemView.setValue(item);
        itemView.addCloseHandler(event -> removeValue(event.getTarget()));

        item.setLink(caseLinkProvider.getLink(item.getType(), isCrmLink(item) ? item.getCaseInfo().getCaseNumber().toString() : item.getRemoteId() ));

        itemToViewModel.put(item, itemView);
        linksContainer.add(itemView);
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

    @Inject
    CaseLinkProvider caseLinkProvider;
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

    private boolean enabled = true;
    private Map<CaseLink, IssueLink> itemToViewModel = new HashMap<>();
    private Set<CaseLink> items = null;

    interface IssueLinksViewUiBinder extends UiBinder<HTMLPanel, IssueLinks> {}
    private static IssueLinksViewUiBinder ourUiBinder = GWT.create(IssueLinksViewUiBinder.class);
}
