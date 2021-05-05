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
import ru.protei.portal.ui.common.client.activity.projectsearch.AbstractProjectSearchView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.XL_MODAL;

abstract public class ProjectWidget extends Composite implements HasValue<ProjectInfo>, HasEnabled, HasValidable,
        Activity {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        name.getElement().setAttribute("placeholder", lang.selectContractProject());
        root.setTitle(lang.selectContractProject());
        prepareDialog(dialogView);
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
        name.setValue(value != null ? value.getName() + " (#" + value.getId() + ")": null, fireEvents);
        if (isValidable) {
            setValid(isValid());
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public boolean isEnabled() {
        return search.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        search.setEnabled(enabled);
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
            name.removeStyleName(REQUIRED);
        } else {
            name.addStyleName(REQUIRED);
        }
    }

    public void setMandatory( boolean mandatory ) {
        if ( mandatory ) {
            root.addStyleName(REQUIRED);
        } else {
            root.removeStyleName(REQUIRED);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ProjectInfo> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("search")
    public void onSearchClicked(ClickEvent event ) {
        fireEvent(new ProjectEvents.Search(dialogView.getBodyContainer(), false, true));
        dialogView.showPopup();
    }

    @UiHandler("reset")
    public void onResetClicked(ClickEvent event ) {
        setValue(null, true);
    }

    public void setEnsureDebugId( String debugId ) {
        search.ensureDebugId(debugId);
    }

    private void ensureDebugIds() {
        name.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.PROJECT.NAME);
        search.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.PROJECT.SEARCH_BUTTON);
        reset.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.PROJECT.RESET_BUTTON);
    }

    private void prepareDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(makeSearchDialogActivity());
        dialog.addStyleName(XL_MODAL);
        dialog.removeButtonVisibility().setVisible(false);
        dialog.setHeader(lang.searchProjectTitle());
        dialog.setSaveButtonName(lang.buttonApply());
        dialog.setAdditionalVisible(false);
    }

    private AbstractDialogDetailsActivity makeSearchDialogActivity() {
        return new AbstractDialogDetailsActivity(){
            @Override
            public void onSaveClicked() {
                if (searchView.project().getValue() == null) {
                    fireEvent(new NotifyEvents.Show(lang.contractProjectFindNotChosenError(), NotifyEvents.NotifyType.INFO));
                    return;
                }
                setValue(searchView.project().getValue(), true);
                onCancelClicked();
            }
            @Override
            public void onCancelClicked() {
                dialogView.hidePopup();
            }
        };
    }

    @Inject
    AbstractProjectSearchView searchView;

    @Inject
    AbstractDialogDetailsView dialogView;

    @UiField
    HTMLPanel root;

    @UiField
    TextBox name;

    @UiField
    Button search;
    @UiField
    Button reset;

    @UiField
    Lang lang;

    private ProjectInfo value;
    private boolean isValidable;

    interface ProjectWidgetUiBinder extends UiBinder<HTMLPanel, ProjectWidget> {}
    private static ProjectWidgetUiBinder ourUiBinder = GWT.create( ProjectWidgetUiBinder.class );
}