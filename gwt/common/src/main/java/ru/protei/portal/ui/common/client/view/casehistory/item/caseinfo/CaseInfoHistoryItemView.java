package ru.protei.portal.ui.common.client.view.casehistory.item.caseinfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.casehistory.item.caseinfo.AbstractCaseInfoHistoryItemView;

public class CaseInfoHistoryItemView extends Composite implements AbstractCaseInfoHistoryItemView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setDescription(String issueDescription) {
        descriptionReadOnly.getElement().setInnerHTML(issueDescription);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        descriptionReadOnly.ensureDebugId(DebugIds.ISSUE.DESCRIPTION_FIELD );
    }

    @UiField
    HTMLPanel descriptionReadOnly;

    interface CaseInfoHistoryItemViewUiBinder extends UiBinder<HTMLPanel, CaseInfoHistoryItemView> {}
    private static CaseInfoHistoryItemViewUiBinder ourUiBinder = GWT.create(CaseInfoHistoryItemViewUiBinder.class);
}
