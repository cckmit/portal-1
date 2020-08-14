package ru.protei.portal.ui.common.client.widget.accordion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AccordionWidget extends Composite {
    private static AccordionWidgetUiBinder ourUiBinder = GWT.create(AccordionWidgetUiBinder.class);

    public AccordionWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
    interface AccordionWidgetUiBinder extends UiBinder<HTMLPanel, AccordionWidget> {}
}
