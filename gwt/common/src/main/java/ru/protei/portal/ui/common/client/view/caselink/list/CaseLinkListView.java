package ru.protei.portal.ui.common.client.view.caselink.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselink.list.AbstractCaseLinkListActivity;
import ru.protei.portal.ui.common.client.activity.caselink.list.AbstractCaseLinkListView;
import ru.protei.portal.ui.common.client.widget.caselink.popup.CreateCaseLinkPopup;



public class CaseLinkListView
        extends Composite
        implements AbstractCaseLinkListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initDebugIds();

        createCaseLinkPopup.addValueChangeHandler(event -> activity.onAddLinkClicked(event.getValue()));
    }

    @Override
    public void setActivity(AbstractCaseLinkListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setLinksContainerVisible(boolean isVisible) {
        if (isVisible) {
            getElement().replaceClassName("collapsed", "expanded");
        } else {
            getElement().replaceClassName( "expanded", "collapsed");
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

    @Override
    public HasVisibility addButtonVisibility() {
        return addLinkButton;
    }

    @UiHandler("addLinkButton")
    public void addLinkButtonClick(ClickEvent event) {
        event.preventDefault();
        createCaseLinkPopup.resetValueAndShow(addLinkButton);
    }

    @UiHandler("collapse")
    public void onChangeFormStateClicked(ClickEvent event) {
        event.preventDefault();
        activity.onLinksContainerStateChanged(!getStyleName().contains("expanded"));
    }

    private void initDebugIds() {
        headerLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.LINKS);
        addLinkButton.ensureDebugId(DebugIds.ISSUE.LINKS_BUTTON);
        linksPanel.ensureDebugId(DebugIds.ISSUE.LINKS_CONTAINER);
        collapse.ensureDebugId(DebugIds.ISSUE.LINKS_COLLAPSE_BUTTON);

        createCaseLinkPopup.setEnsureDebugIdSelector(DebugIds.ISSUE.LINKS_TYPE_SELECTOR);
        createCaseLinkPopup.setEnsureDebugIdTextBox(DebugIds.ISSUE.LINKS_INPUT);
        createCaseLinkPopup.setEnsureDebugIdApply(DebugIds.ISSUE.LINKS_APPLY_BUTTON);
    }

    @UiField
    Anchor addLinkButton;
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