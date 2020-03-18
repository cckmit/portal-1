package ru.protei.portal.ui.ipreservation.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.switcher.Switcher;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.ipreservation.client.activity.edit.AbstractSubnetEditActivity;
import ru.protei.portal.ui.ipreservation.client.activity.edit.AbstractSubnetEditView;

import java.util.Set;

/**
 * Вид создания и редактирования подсети
 */
public class SubnetEditView extends Composite implements AbstractSubnetEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractSubnetEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> address() { return address; }

    @Override
    public HasValue<String> mask() { return mask; }

    @Override
    public HasValue<Boolean> local() { return local; }

    @Override
    public HasText comment() { return comment; }

    @Override
    public HasValidable addressValidator() {
        return address;
    }

    @Override
    public HasValidable maskValidator() {
        return mask;
    }

    @Override
    public HasVisibility saveVisibility() {
        return saveButton;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        address.ensureDebugId(DebugIds.SUBNET.ADDRESS_INPUT);
        mask.ensureDebugId(DebugIds.SUBNET.MASK_INPUT);
        comment.ensureDebugId(DebugIds.SUBNET.COMMENT_INPUT);
        local.ensureDebugId(DebugIds.SUBNET.LOCAL_SWITCHER);
        saveButton.ensureDebugId(DebugIds.SUBNET.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.SUBNET.CANCEL_BUTTON);
    }

    @UiField
    ValidableTextBox address;
    @UiField
    ValidableTextBox mask;
    @UiField
    TextArea comment;
    @UiField
    Switcher local;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;

    private AbstractSubnetEditActivity activity;

    interface SubnetEditViewUiBinder extends UiBinder<HTMLPanel, SubnetEditView> {}
    private static SubnetEditViewUiBinder ourUiBinder = GWT.create(SubnetEditViewUiBinder.class);
}