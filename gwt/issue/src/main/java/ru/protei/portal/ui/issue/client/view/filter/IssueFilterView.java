package ru.protei.portal.ui.issue.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterParamView;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

/**
 * Представление фильтра обращений
 */
public class IssueFilterView extends Composite implements AbstractIssueFilterView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        issueFilterParamView.commentAuthorsVisibility().setVisible(false);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    @Override
    public void setActivity(AbstractIssueFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public AbstractIssueFilterWidgetView getIssueFilterWidget() {
        return issueFilterParamView;
    }

    @Override
    public void resetFilter() {
        issueFilterParamView.resetFilter();
    }

    @Override
    public void changeUserFilterValueName( CaseFilterShortView value ){
        issueFilterParamView.changeUserFilterValueName( value );
    }

    @Override
    public void addUserFilterDisplayOption( CaseFilterShortView value ){
        issueFilterParamView.addUserFilterDisplayOption( value );
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
        filterCollapseBtn.ensureDebugId(DebugIds.FILTER.COLLAPSE_BUTTON);
        filterRestoreBtn.ensureDebugId(DebugIds.FILTER.RESTORE_BUTTON);
    }

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    IssueFilterParamView issueFilterParamView;

    @UiField
    Anchor filterRestoreBtn;
    @UiField
    Anchor filterCollapseBtn;


    @Inject
    FixedPositioner positioner;

    private AbstractIssueFilterActivity activity;

    private static IssueFilterView.IssueFilterViewUiBinder ourUiBinder = GWT.create( IssueFilterView.IssueFilterViewUiBinder.class );
    interface IssueFilterViewUiBinder extends UiBinder<HTMLPanel, IssueFilterView > {}
}