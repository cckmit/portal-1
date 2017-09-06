package ru.protei.portal.ui.official.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.official.client.activity.edit.AbstractOfficialMemberEditView;
import ru.protei.portal.ui.official.client.activity.edit.AbstractOfficialMemberEditActivity;

import java.util.Arrays;

/**
 * Представление формы редактирования должностного лица
 */
public class OfficialMemberEditView extends Composite implements AbstractOfficialMemberEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        company.setDefaultValue( lang.selectOfficialCompany() );
        company.setCategories(Arrays.asList(En_CompanyCategory.OFFICIAL));
        company.subscribeToModel();
        fillAmpluaOptions();

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
    public HasValue<En_DevUnitPersonRoleType> amplua() { return amplua; }

    @Override
    public HasValue<String> comments() { return comments; }

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

    private void fillAmpluaOptions() {
        amplua.addOption( lang.selectOfficialAmplua(), null );
        for (En_DevUnitPersonRoleType roleType: En_DevUnitPersonRoleType.values()) {
            if (roleType.equals(En_DevUnitPersonRoleType.HEAD_MANAGER) ||
                roleType.equals(En_DevUnitPersonRoleType.DEPLOY_MANAGER)) {
                continue;
            }
            amplua.addOption(roleTypeLang.getName(roleType), roleType);
        }
    }

    @UiField
    Lang lang;

    @Inject
    En_PersonRoleTypeLang roleTypeLang;

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
    @Inject
    @UiField(provided = true)
    ButtonSelector<En_DevUnitPersonRoleType> amplua;
    @UiField
    TextBox firstName;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @UiField
    TextArea comments;

    private static OfficialMemberEditActivityUiBinder ourUiBinder = GWT.create(OfficialMemberEditActivityUiBinder.class);

    private AbstractOfficialMemberEditActivity activity;

    interface OfficialMemberEditActivityUiBinder extends UiBinder<HTMLPanel, OfficialMemberEditView> {}
}