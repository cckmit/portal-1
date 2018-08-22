package ru.protei.portal.ui.sitefolder.client.view.platform.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.AbstractPlatformEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.AbstractPlatformEditView;

public class PlatformEditView extends Composite implements AbstractPlatformEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractPlatformEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<String> parameters() {
        return parameters;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasWidgets listContainer() {
        return listContainer;
    }

    @Override
    public HasVisibility listContainerVisibility() {
        return listContainer;
    }

    @Override
    public HasVisibility listContainerHeaderVisibility() {
        return listContainerHeader;
    }

    @Override
    public HasEnabled companyEnabled() {
        return company;
    }

    @Override
    public HasValidable nameValidator() {
        return name;
    }

    @Override
    public HasValidable companyValidator() {
        return company;
    }

    @Override
    public HasVisibility openButtonVisibility() {
        return openButton;
    }

    @Override
    public HasVisibility createButtonVisibility() {
        return createButton;
    }

    @Override
    public HasWidgets contactsContainer() {
        return contactsContainer;
    }

    @UiHandler("saveButton")
    public void saveButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void cancelButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiHandler("openButton")
    public void openButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOpenClicked();
        }
    }

    @UiHandler("createButton")
    public void createButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCreateClicked();
        }
    }

    @UiHandler("company")
    public void onCompanySelected(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onCompanySelected();
        }
    }

    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    CompanySelector company;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;
    @UiField
    TextArea parameters;
    @UiField
    TextArea comment;
    @UiField
    HTMLPanel listContainerHeader;
    @UiField
    HTMLPanel listContainer;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @UiField
    Button createButton;
    @UiField
    Button openButton;
    @UiField
    HTMLPanel contactsContainer;

    private AbstractPlatformEditActivity activity;

    interface SiteFolderEditViewUiBinder extends UiBinder<HTMLPanel, PlatformEditView> {}
    private static SiteFolderEditViewUiBinder ourUiBinder = GWT.create(SiteFolderEditViewUiBinder.class);
}
