package ru.protei.portal.ui.common.client.widget.collapse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import java.util.Iterator;

public class CollapsablePanel extends Composite implements HasWidgets {

    public CollapsablePanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        legend.addDomHandler(event -> {
            event.preventDefault();
            if (collapseContainer.hasClassName("show")) {
                collapseContainer.removeClassName("show");
            } else {
                collapseContainer.addClassName("show");
            }
        }, ClickEvent.getType());
    }

    @Override
    public void add(Widget w) {
        container.add(w);
    }

    @Override
    public void clear() {
        container.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return container.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return container.remove(w);
    }

    public void setLegend(String legend) {
        this.legend.getElement().setInnerHTML(legend);
    }

    @UiField
    HTMLPanel container;
    @UiField
    Anchor legend;
    @UiField
    DivElement collapseContainer;

    interface CollapseContainerUiBinder extends UiBinder<HTMLPanel, CollapsablePanel> {}

    private static CollapseContainerUiBinder ourUiBinder = GWT.create(CollapseContainerUiBinder.class);

}