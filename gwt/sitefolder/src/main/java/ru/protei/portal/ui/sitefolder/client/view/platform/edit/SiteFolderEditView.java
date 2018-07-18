package ru.protei.portal.ui.sitefolder.client.view.platform.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.AbstractSiteFolderEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.AbstractSiteFolderEditView;

public class SiteFolderEditView extends Composite implements AbstractSiteFolderEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractSiteFolderEditActivity activity) {
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

    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    CompanySelector company;
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

    private AbstractSiteFolderEditActivity activity;

    interface SiteFolderEditViewUiBinder extends UiBinder<HTMLPanel, SiteFolderEditView> {}
    private static SiteFolderEditViewUiBinder ourUiBinder = GWT.create(SiteFolderEditViewUiBinder.class);
}
