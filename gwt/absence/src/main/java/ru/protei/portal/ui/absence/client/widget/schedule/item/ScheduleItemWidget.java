package ru.protei.portal.ui.absence.client.widget.schedule.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import ru.protei.portal.ui.common.client.widget.selector.event.HasRemoveHandlers;
import ru.protei.portal.ui.common.client.widget.selector.event.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.selector.event.RemoveHandler;

public class ScheduleItemWidget
        extends Composite
        implements HasRemoveHandlers {

    public ScheduleItemWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setDays(String value) {
        days.setText(value);
    }

    public void setTimes(String value) {
        times.setText(value);
    }

    @Override
    public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
        return addHandler(handler, RemoveEvent.getType());
    }

    @UiHandler("remove")
    public void onRemoveClicked(ClickEvent event) {
        RemoveEvent.fire(this);
    }

    @UiField
    Label days;
    @UiField
    Label times;
    @UiField
    Anchor remove;

    interface AbsenceCreateWidgetBinder extends UiBinder<HTMLPanel, ScheduleItemWidget> {}
    private static AbsenceCreateWidgetBinder ourUiBinder = GWT.create(AbsenceCreateWidgetBinder.class);
}
