package ru.protei.portal.ui.common.client.view.caselink.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.caselink.list.AbstractCaseLinkListActivity;
import ru.protei.portal.ui.common.client.activity.caselink.list.AbstractCaseLinkListView;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.widget.caselink.popup.CreateCaseLinkPopup;

import java.util.Collection;


public class CaseLinkListView
        extends Composite
        implements AbstractCaseLinkListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        createCaseLinkPopup.addValueChangeHandler(event -> activity.onAddLinkClicked(event.getValue()));
    }

    @Override
    public void setActivity(AbstractCaseLinkListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setLinksContainerVisible(boolean isVisible) {
        linksPanel.setVisible(isVisible);

        if (isVisible) {
            collapse.getElement().replaceClassName("fas fa-chevron-down", "fas fa-chevron-up");
        } else {
            collapse.getElement().replaceClassName("fas fa-chevron-up", "fas fa-chevron-down");
        }
    }

    @Override
    public HasWidgets getLinksContainer() {
        return linksPanel;
    }

    @Override
    public void setHeader(String value) {
        headerLabel.setInnerText(value);
    }

    @UiHandler("addLinkButton")
    public void addLinkButtonClick(ClickEvent e) {
        createCaseLinkPopup.showNear(addLinkButton);
    }

    @UiHandler("collapse")
    public void onChangeFormStateClicked(ClickEvent event) {
        event.preventDefault();
        activity.onLinksContainerStateChanged(!linksPanel.isVisible());
    }

    @UiField
    Button addLinkButton;
    @UiField
    HTMLPanel linksPanel;
    @UiField
    Anchor collapse;
    @UiField
    LabelElement headerLabel;
    @Inject
    CreateCaseLinkPopup createCaseLinkPopup;

    private AbstractCaseLinkListActivity activity;

    private static CaseLinkListUiBinder ourUiBinder = GWT.create(CaseLinkListUiBinder.class);
    interface CaseLinkListUiBinder extends UiBinder<HTMLPanel, CaseLinkListView> {}
}