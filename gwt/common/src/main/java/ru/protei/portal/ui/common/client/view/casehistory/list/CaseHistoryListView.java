package ru.protei.portal.ui.common.client.view.casehistory.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.casehistory.list.AbstractCaseHistoryListActivity;
import ru.protei.portal.ui.common.client.activity.casehistory.list.AbstractCaseHistoryListView;

/**
 * Контейнер для истории
 */

public class CaseHistoryListView
        extends Composite
        implements AbstractCaseHistoryListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractCaseHistoryListActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets.ForIsWidget root() {
        return root;
    }

    @UiField
    HTMLPanel root;

    private AbstractCaseHistoryListActivity activity;

    private static CaseHistoryListUiBinder ourUiBinder = GWT.create(CaseHistoryListUiBinder.class);
    interface CaseHistoryListUiBinder extends UiBinder<HTMLPanel, CaseHistoryListView> {}
}
