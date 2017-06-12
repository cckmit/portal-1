package ru.protei.portal.ui.role.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.privilege.list.PrivilegeList;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.GenderButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.role.client.activity.edit.AbstractRoleEditActivity;
import ru.protei.portal.ui.role.client.activity.edit.AbstractRoleEditView;

import java.util.Date;

/**
 * Представление создания и редактирования роли
 */
public class RoleEditView extends Composite implements AbstractRoleEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity(AbstractRoleEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<String> description() {
        return description;
    }

    @UiHandler( "saveButton" )
    public void onSaveClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSaveClicked();
        }
    }

    @UiHandler( "cancelButton" )
    public void onCancelClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCancelClicked();
        }
    }

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;
    @UiField
    ValidableTextBox name;
    @UiField
    TextBox description;
    @Inject
    @UiField(provided = true)
    PrivilegeList privileges;


    AbstractRoleEditActivity activity;

    private static ContactViewUiBinder ourUiBinder = GWT.create(ContactViewUiBinder.class);
    interface ContactViewUiBinder extends UiBinder<HTMLPanel, RoleEditView > {}
}
