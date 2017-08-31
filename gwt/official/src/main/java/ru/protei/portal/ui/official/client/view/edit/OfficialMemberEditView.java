package ru.protei.portal.ui.official.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.official.client.activity.edit.AbstractOfficialMemberEditView;
import ru.protei.portal.ui.official.client.activity.edit.AbstractOfficialMemberEditActivity;

/**
 * Представление формы редактирования должностного лица
 */
public class OfficialMemberEditView extends Composite implements AbstractOfficialMemberEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        company.setDefaultValue( lang.selectOfficialCompany() );
    }

    @Override
    public HasValue<String> lastName() {
        return lastName;
    }

    @Override
    public HasValue<String> firstName() {
        return firstName;
    }

    @Override
    public HasValue<String> secondName() {
        return secondName;
    }

    @Override
    public HasValue<EntityOption> organization() {
        return company;
    }

    @Override
    public HasValue<String> position() {
        return position;
    }

    @Override
    public HasValue<String> relations() {
        return relations;
    }

    @Override
    public HasValue<String> amplua() {
        return amplua;
    }

    @Override
    public void setActivity(AbstractOfficialMemberEditActivity activity) {
        this.activity = activity;
    }

    @UiHandler("saveButton")
    public void onSaveButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    @UiField
    TextBox lastName;
    @UiField
    TextBox secondName;
    @UiField
    TextBox position;
    @UiField
    TextBox relations;
    @UiField
    TextArea amplua;
    @UiField
    TextBox firstName;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    private static OfficialMemberEditActivityUiBinder ourUiBinder = GWT.create(OfficialMemberEditActivityUiBinder.class);

    private AbstractOfficialMemberEditActivity activity;

    interface OfficialMemberEditActivityUiBinder extends UiBinder<HTMLPanel, OfficialMemberEditView> {}
}