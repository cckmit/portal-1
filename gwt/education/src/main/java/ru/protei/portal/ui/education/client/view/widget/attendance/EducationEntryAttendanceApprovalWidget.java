package ru.protei.portal.ui.education.client.view.widget.attendance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Map;

import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.education.client.model.Approve;
import ru.protei.portal.ui.education.client.view.widget.attendance.item.EducationEntryAttendanceApprovalItem;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public class EducationEntryAttendanceApprovalWidget extends Composite implements HasValue<Map<EducationEntryAttendance, Approve>> {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public Map<EducationEntryAttendance, Approve> getValue() {
        return value;
    }

    @Override
    public void setValue(Map<EducationEntryAttendance, Approve> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Map<EducationEntryAttendance, Approve> value, boolean fireEvents) {
        this.value = value;
        draw();
        if (fireEvents) {
            ValueChangeEvent.fire(this, this.value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Map<EducationEntryAttendance, Approve>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void draw() {
        root.clear();
        root.add(makeLabelView());
        emptyIfNull(value).forEach((a, s) -> root.add(makeEntryView(a, s)));
    }

    private Widget makeLabelView() {
        HTMLPanel div = new HTMLPanel("div", "");
        div.setStyleName("form-group col-md-12");
        div.add(new HTMLPanel("span", lang.educationEntryParticipants()));
        return div;
    }

    private Widget makeEntryView(EducationEntryAttendance attendance, Approve state) {
        EducationEntryAttendanceApprovalItem item = itemProvider.get();
        item.setPerson(attendance.getWorkerName());
        item.setApproved(state != Approve.APPROVED_FINAL && state != Approve.APPROVED_FINAL_DECLINED, state == Approve.APPROVED);
        item.setDeclined(true, state == Approve.DECLINED || state == Approve.APPROVED_FINAL_DECLINED);
        item.setHandler(new EducationEntryAttendanceApprovalItem.Handler() {
            public void onApproved() {
                updateState(state == Approve.APPROVED
                            ? Approve.UNKNOWN
                            : Approve.APPROVED);
            }
            public void onDeclined() {
                updateState(state == Approve.APPROVED_FINAL
                            ? Approve.APPROVED_FINAL_DECLINED
                            : state == Approve.APPROVED_FINAL_DECLINED
                                ? Approve.APPROVED_FINAL
                                : state == Approve.DECLINED
                                    ? Approve.UNKNOWN
                                    : Approve.DECLINED);
            }
            private void updateState(Approve state) {
                for (Map.Entry<EducationEntryAttendance, Approve> entry : value.entrySet()) {
                    if (entry.getKey() == attendance) {
                        entry.setValue(state);
                        break;
                    }
                }
                draw();
            }
        });
        return item.asWidget();
    }

    private

    @Inject
    Lang lang;
    @Inject
    Provider<EducationEntryAttendanceApprovalItem> itemProvider;

    @UiField
    HTMLPanel root;

    private Map<EducationEntryAttendance, Approve> value;

    interface EducationEntryAttendanceApprovalWidgetBinder extends UiBinder<HTMLPanel, EducationEntryAttendanceApprovalWidget> {}
    private static EducationEntryAttendanceApprovalWidgetBinder ourUiBinder = GWT.create(EducationEntryAttendanceApprovalWidgetBinder.class);
}
