package ru.protei.portal.ui.common.client.widget.collapse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.test.client.DebugIds;

import java.util.Iterator;

public class CollapsiblePanel extends Composite implements HasWidgets {

    public CollapsiblePanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void add(Widget w) {
        panelBody.add(w);
    }

    @Override
    public void clear() {
        panelBody.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return panelBody.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return panelBody.remove(w);
    }

    @UiHandler("collapse")
    public void onCollapseClicked(ClickEvent event) {
        event.preventDefault();
        setPanelVisible(!panelBody.isVisible());
    }

    public boolean isPanelBodyVisible() {
        return panelBody.isVisible();
    }

    public void setPanelBodyVisible(boolean isVisible) {
        setPanelVisible(isVisible);
    }

    public void setLabelDebugId(String debugId) {
        headerLabel.setId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }

    public void setLabel(String label) {
        this.headerLabel.setInnerText(label);
    }

    private void setPanelVisible(boolean isVisible) {
        panelBody.setVisible(isVisible);

        if (panelBody.isVisible()) {
            collapse.getElement().replaceClassName("fas fa-chevron-down", "fas fa-chevron-up");
        } else {
            collapse.getElement().replaceClassName("fas fa-chevron-up", "fas fa-chevron-down");
        }
    }

    @UiField
    HTMLPanel collapsiblePanel;

    @UiField
    LabelElement headerLabel;

    @UiField
    HTMLPanel panelBody;

    @UiField
    Anchor collapse;

    interface CollapseContainerUiBinder extends UiBinder<HTMLPanel, CollapsiblePanel> {}

    private static CollapseContainerUiBinder ourUiBinder = GWT.create(CollapseContainerUiBinder.class);

}