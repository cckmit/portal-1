package ru.protei.portal.ui.delivery.client.widget.rfidlabel.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.delivery.client.activity.rfidlabels.table.filter.AbstractRFIDLabelParamWidget;

import static ru.protei.portal.core.model.dict.En_SortField.id;

public class RFIDLabelParamWidget extends Composite implements AbstractRFIDLabelParamWidget {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        resetFilter();
    }

    public void setOnFilterChangeCallback(Runnable onFilterChangeCallback) {
        this.onFilterChangeCallback = onFilterChangeCallback;
    }

    @Override
    public void resetFilter() {
        epc.setValue(null);
        name.setValue(null);
        if (isAttached()) {
            onFilterChanged();
        }
    }

    @Override
    public RFIDLabelQuery getQuery() {
        RFIDLabelQuery query = new RFIDLabelQuery();
        query.setEpc(epc.getValue());
        query.setName(name.getValue());
        query.setSortField(id);
        query.setSortDir(En_SortDir.ASC);
        return query;
    }

    @UiHandler("epc")
    public void onEpcChanged(ValueChangeEvent<String> event) {
        onFilterChanged();
    }

    @UiHandler("name")
    public void onNameChanged(ValueChangeEvent<String> event) {
        onFilterChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        epc.setDebugIdTextBox(DebugIds.FILTER.RFID_LABEL_EPC_SEARCH_INPUT);
        epc.setDebugIdAction(DebugIds.FILTER.RFID_LABEL_EPC_SEARCH_CLEAR_BUTTON);
        name.setDebugIdTextBox(DebugIds.FILTER.RFID_LABEL_NAME_SEARCH_INPUT);
        name.setDebugIdAction(DebugIds.FILTER.RFID_LABEL_NAME_SEARCH_CLEAR_BUTTON);
    }


    private void onFilterChanged() {
        if (onFilterChangeCallback != null) {
            onFilterChangeCallback.run();
        }
    }

    @UiField
    CleanableSearchBox epc;
    @UiField
    CleanableSearchBox name;
    @UiField
    Lang lang;

    private Runnable onFilterChangeCallback;

    private static RFIDLabelParamWidgetUiBinder ourUiBinder = GWT.create(RFIDLabelParamWidgetUiBinder.class);
    interface RFIDLabelParamWidgetUiBinder extends UiBinder<HTMLPanel, RFIDLabelParamWidget> {}
}
