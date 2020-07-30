package ru.protei.portal.ui.common.client.widget.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.projectsearch.AbstractProjectSearchActivity;
import ru.protei.portal.ui.common.client.activity.projectsearch.AbstractProjectSearchView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;


import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.WIDE_MODAL;

abstract public class ProjectWidget extends Composite implements HasValue<ProjectInfo>, HasEnabled, HasValidable,
        Activity {

    @Inject
    public void onInit(AbstractProjectSearchActivity activity) {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        name.getElement().setAttribute("placeholder", lang.selectContractProject());
        root.setTitle(lang.selectContractProject());
        searchView.setActivity(activity);
        prepareSearchDialog(dialogDetailsSearchView);
    }

    @Override
    public ProjectInfo getValue() {
        return value;
    }

    @Override
    public void setValue(ProjectInfo value) {
        setValue(value, false);
    }

    @Override
    public void setValue(ProjectInfo value, boolean fireEvents) {
        this.value = value;
        name.setValue(value != null ? value.getName() : null, fireEvents);
        if (isValidable) {
            setValid(isValid());
        }
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }

    @Override
    public boolean isValid(){
        return getValue() != null;
    }

    @Override
    public void setValid(boolean isValid) {
        if (isValid) {
            name.removeStyleName(REQUIRED_STYLE_NAME);
        } else {
            name.addStyleName(REQUIRED_STYLE_NAME);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ProjectInfo> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler( "button" )
    public void onButtonClicked ( ClickEvent event ) {
        searchView.resetFilter();
        searchView.clearProjectList();
        dialogDetailsSearchView.showPopup();
    }

    private void ensureDebugIds() {
        name.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.PROJECT.NAME);
        button.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.PROJECT.SEARCH_BUTTON);
    }

    public void setEnsureDebugId( String debugId ) {
        button.ensureDebugId(debugId);
    }

    private void prepareSearchDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(makeSearchDialogActivity());
        dialog.addStyleName(WIDE_MODAL);
        dialog.getBodyContainer().add(searchView.asWidget());
        dialog.removeButtonVisibility().setVisible(false);
        dialog.setHeader(lang.searchProjectTitle());
        dialog.setSaveButtonName(lang.buttonApply());
        dialog.setAdditionalVisible(false);
        searchView.setVisibleProducts(false);
        searchView.setVisibleManagers(true);
        searchView.setDialogViewStyles();
    }

    private AbstractDialogDetailsActivity makeSearchDialogActivity() {
        return new AbstractDialogDetailsActivity(){
            @Override
            public void onSaveClicked() {
                if (searchView.project().getValue() == null) {
                    fireEvent(new NotifyEvents.Show(lang.contractProjectFindNotChosenError(), NotifyEvents.NotifyType.INFO));
                    return;
                }
                setValue(searchView.project().getValue());
                dialogDetailsSearchView.hidePopup();
            }
            @Override
            public void onCancelClicked() {
                dialogDetailsSearchView.hidePopup();
            }
        };
    }

    @Inject
    AbstractProjectSearchView searchView;

    @Inject
    AbstractDialogDetailsView dialogDetailsSearchView;

    @UiField
    HTMLPanel root;

    @UiField
    TextBox name;

    @UiField
    Button button;

    @UiField
    Lang lang;

    private ProjectInfo value;
    private boolean isValidable;

    private static final String REQUIRED_STYLE_NAME = "required";

    interface ProjectWidgetUiBinder extends UiBinder<HTMLPanel, ProjectWidget> {}
    private static ProjectWidgetUiBinder ourUiBinder = GWT.create( ProjectWidgetUiBinder.class );
}