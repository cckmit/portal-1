package ru.protei.portal.ui.education.client.view.widget.attendance.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;

public class EducationEntryAttendanceApprovalItem extends Composite {

    public interface Handler {
        void onApproved();
        void onDeclined();
    }

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setPerson(String name) {
        label.setInnerText(name);
    }

    public void setApproved(boolean isActive, boolean isSelected) {
        btnAccept.removeStyleName("btn-success");
        btnAccept.removeStyleName("btn-complete");
        btnAccept.removeStyleName("btn-default");
        btnAccept.addStyleName(!isActive ? "btn-default" : isSelected ? "btn-complete" : "btn-success");
        btnAccept.setEnabled(isActive);
    }

    public void setDeclined(boolean isActive, boolean isSelected) {
        btnDecline.removeStyleName("btn-danger");
        btnDecline.removeStyleName("btn-complete");
        btnDecline.removeStyleName("btn-default");
        btnDecline.addStyleName(!isActive ? "btn-default" : isSelected ? "btn-complete" : "btn-danger");
        btnDecline.setEnabled(isActive);
    }

    @UiHandler("btnAccept")
    public void btnAcceptClick(ClickEvent event) {
        if (handler != null) {
            handler.onApproved();
        }
    }

    @UiHandler("btnDecline")
    public void btnDeclineClick(ClickEvent event) {
        if (handler != null) {
            handler.onDeclined();
        }
    }

    @UiField
    Button btnDecline;
    @UiField
    Button btnAccept;
    @UiField
    SpanElement label;

    private Handler handler;

    interface EducationEntryAttendanceApprovalItemBinder extends UiBinder<HTMLPanel, EducationEntryAttendanceApprovalItem> {}
    private static EducationEntryAttendanceApprovalItemBinder ourUiBinder = GWT.create(EducationEntryAttendanceApprovalItemBinder.class);
}
