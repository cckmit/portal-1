package ru.protei.portal.ui.common.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueCollapseFilterView;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterCollapseActivity;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Представление фильтра обращений
 */
public class IssueFilterCollapseView extends Composite implements AbstractIssueCollapseFilterView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractIssueFilterCollapseActivity activity) {
        this.activity = activity;
    }

    @Override
    public AbstractIssueFilterView getIssueFilterParamView() {
        return issueFilterParamView;
    }

    @UiHandler("filterRestoreBtn")
    public void onFilterRestoreBtnClick(ClickEvent event) {
        if (activity != null) {
            activity.onFilterRestore();
        }
    }

    @UiHandler("filterCollapseBtn")
    public void onFilterCollapseBtnClick(ClickEvent event) {
        if (activity != null) {
            activity.onFilterCollapse();
        }
    }

    private void ensureDebugIds() {
        labelFilters.setId(DebugIds.FILTER.FILTERS_LABEL);
        filterCollapseBtn.ensureDebugId(DebugIds.FILTER.COLLAPSE_BUTTON);
        filterRestoreBtn.ensureDebugId(DebugIds.FILTER.RESTORE_BUTTON);
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    IssueFilterView issueFilterParamView;
    @UiField
    Anchor filterRestoreBtn;
    @UiField
    Anchor filterCollapseBtn;
    @UiField
    LabelElement labelFilters;

    private AbstractIssueFilterCollapseActivity activity;

    private static IssueFilterCollapseView.IssueFilterViewUiBinder ourUiBinder = GWT.create( IssueFilterCollapseView.IssueFilterViewUiBinder.class );
    interface IssueFilterViewUiBinder extends UiBinder<HTMLPanel, IssueFilterCollapseView> {}
}