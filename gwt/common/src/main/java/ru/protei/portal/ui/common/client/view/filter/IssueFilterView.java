package ru.protei.portal.ui.common.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterCollapseActivity;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterParamView;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterView;

import java.util.function.Consumer;

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
        issueFilterParamView.watchForScrollOf(root);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        issueFilterParamView.stopWatchForScrollOf(root);
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
        removeBtn.setVisible(false);
        saveBtn.setVisible(false);
        createBtn.setVisible(true);
        filterName.removeStyleName(REQUIRED);
        filterName.setValue("");
    }

    @Override
    public HasEnabled createEnabled() {
        return createBtn;
    }

    @Override
    public void changeUserFilterValueName( CaseFilterShortView value ){
        issueFilterParamView.changeUserFilterValueName( value );
    }

    @Override
    public void addUserFilterDisplayOption( CaseFilterShortView value ){
        issueFilterParamView.addUserFilterDisplayOption( value );
    }

    @Override
    public HasVisibility removeFilterBtnVisibility(){
        return removeBtn;
    }

    @Override
    public HasValue< String > filterName() {
        return filterName;
    }

    @Override
    public void setFilterNameContainerErrorStyle( boolean hasError ) {
        if ( hasError ) {
            filterName.addStyleName(REQUIRED);
        } else {
            filterName.removeStyleName( REQUIRED );
        }
    }

    @Override
    public void setUserFilterNameVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            filterNameContainer.removeClassName( HIDE );
        } else {
            filterNameContainer.addClassName( HIDE );
        }
    }

    @Override
    public void setUserFilterControlsVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            createBtn.removeStyleName( HIDE );
            saveBtn.removeStyleName( HIDE );
            resetBtn.removeStyleName( HIDE );
            removeBtn.removeStyleName( HIDE );
        } else {
            saveBtn.addStyleName( HIDE );
            createBtn.addStyleName( HIDE );
            resetBtn.addStyleName( HIDE );
            removeBtn.addStyleName( HIDE );
        }
    }

    @Override
    public HasVisibility editBtnVisibility() {
        return saveBtn;
    }

    @Override
    public IssueFilterParamView getIssueFilterParams() {
        return issueFilterParamView;
    }

    @Override
    public CaseQuery getValue() {
        return IssueFilterUtils.makeCaseQuery(issueFilterParamView);
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( issueFilterParamView.getActivity() != null ) {
            resetFilter();
            issueFilterParamView.getActivity().onFilterChanged();
        }
    }

    @UiHandler( "saveBtn" )
    public void onSaveClicked ( ClickEvent event ) {
        if ( activity == null ) {
            return;
        }
        activity.onSaveFilterClicked();
    }

    @UiHandler( "createBtn" )
    public void onCreateClicked ( ClickEvent event ) {
        if ( activity == null ) {
            return;
        }
        activity.onCreateFilterClicked();
    }

    @UiHandler( "okBtn" )
    public void onOkBtnClicked ( ClickEvent event ) {
        event.preventDefault();
        if ( activity == null ) {
            return;
        }
        activity.onOkSavingFilterClicked();
    }

    @UiHandler( "cancelBtn" )
    public void onCancelBtnClicked ( ClickEvent event ) {
        event.preventDefault();
        if ( activity == null ) {
            return;
        }
        activity.onCancelSavingFilterClicked();
    }

    @UiHandler( "removeBtn" )
    public void onRemoveClicked ( ClickEvent event ) {
        if (activity == null) {
            return;
        }
        CaseFilterShortView value = issueFilterParamView.userFilter().getValue();
        if (value == null || value.getId() == null) {
            return;
        }
        activity.onFilterRemoveClicked(value.getId());
    }

    @UiHandler( "filterName" )
    public void onFilterNameChanged( KeyUpEvent event ) {
        filterNameChangedTimer.cancel();
        filterNameChangedTimer.schedule( 300 );
    }

    private void ensureDebugIds() {
        filterName.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_NAME_INPUT);
        okBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_OK_BUTTON);
        cancelBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_CANCEL_BUTTON);
        createBtn.ensureDebugId(DebugIds.FILTER.CREATE_BUTTON);
        saveBtn.ensureDebugId(DebugIds.FILTER.SAVE_BUTTON);
        resetBtn.ensureDebugId(DebugIds.FILTER.RESET_BUTTON);
        removeBtn.ensureDebugId(DebugIds.FILTER.REMOVE_BUTTON);
    }

    Timer filterNameChangedTimer = new Timer() {
        @Override
        public void run() {
            setFilterNameContainerErrorStyle( filterName.getValue().isEmpty() );
        }
    };

    public void updateFilterType(En_CaseFilterType filterType) {
        issueFilterParamView.updateFilterType(filterType);
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    IssueFilterParamView issueFilterParamView;
    @UiField
    Button resetBtn;
    @UiField
    Button createBtn;
    @UiField
    Button saveBtn;
    @UiField
    Button removeBtn;
    @UiField
    Anchor okBtn;
    @UiField
    Anchor cancelBtn;
    @UiField
    TextBox filterName;
    @UiField
    DivElement filterNameContainer;

    private AbstractIssueFilterActivity activity;

    private static IssueFilterView.IssueFilterViewUiBinder ourUiBinder = GWT.create( IssueFilterView.IssueFilterViewUiBinder.class );
    interface IssueFilterViewUiBinder extends UiBinder<HTMLPanel, IssueFilterView > {}
}