package ru.protei.portal.ui.common.client.widget.collapse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

public class CollapsablePanel extends Composite implements HasWidgets {
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

    interface CollapseContainerUiBinder extends UiBinder<HTMLPanel, CollapsablePanel> {

    }

    private static CollapseContainerUiBinder ourUiBinder = GWT.create(CollapseContainerUiBinder.class);

    public CollapsablePanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        legend.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (getElement().hasClassName("collapsed")) {
                    getElement().removeClassName("collapsed");
                } else {
                    getElement().addClassName("collapsed");
                }
            }
        }, ClickEvent.getType());
    }

    @UiField
    HTMLPanel container;
    @UiField
    HTMLPanel legend;



}