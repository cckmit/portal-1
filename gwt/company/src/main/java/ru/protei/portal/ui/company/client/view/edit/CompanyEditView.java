package ru.protei.portal.ui.company.client.view.edit;

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
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextArea;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditActivity;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditView;
import ru.protei.portal.ui.company.client.widget.category.buttonselector.CategoryButtonSelector;
import ru.protei.portal.ui.company.client.widget.group.buttonselector.GroupButtonSelector;

/**
 * Вид создания и редактирования компании
 */
public class CompanyEditView extends Composite implements AbstractCompanyEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        companyGroup.setDefaultValue(lang.noCompanyGroup());
    }

    @Override
    public void setActivity( AbstractCompanyEditActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> companyName() {
        return companyName;
    }

    @Override
    public HasValidable companyNameValidator() {
        return companyName;
    }

    @Override
    public void setCompanyNameStatus(NameStatus status) {
        verifiableIcon.setClassName(status.getStyle());
    }

    @Override
    public HasValue<String> actualAddress() {
        return actualAddress;
    }

    @Override
    public HasValidable actualAddressValidator() {
        return actualAddress;
    }

    @Override
    public HasValue<String> legalAddress() {
        return legalAddress;
    }

    @Override
    public HasValidable legalAddressValidator() {
        return legalAddress;
    }

    @Override
    public HasText webSite() {
        return webSite;
    }

    @Override
    public HasText comment() {
        return comment;
    }

    @Override
    public HasValue<CompanyGroup> companyGroup() {
        return companyGroup;
    }

    @Override
    public HasValue<CompanyCategory> companyCategory() {
        return companyCategory;
    }

    @Override
    public HasWidgets phonesContainer() {
        return phonesContainer;
    }

    @Override
    public HasWidgets emailsContainer() {
        return emailsContainer;
    }

    @Override
    public HasWidgets tableContainer() {
        return tableContainer;
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

    @UiHandler("companyName")
    public void onChangeCompanyName( KeyUpEvent keyUpEvent ) {
        verifiableIcon.setClassName(NameStatus.UNDEFINED.getStyle());
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    @UiField
    ValidableTextBox companyName;

    @UiField
    Element verifiableIcon;

    @UiField
    ValidableTextArea actualAddress;

    @UiField
    ValidableTextArea legalAddress;

    @UiField
    TextArea comment;

    @UiField
    TextBox webSite;
    
    @Inject
    @UiField( provided = true )
    GroupButtonSelector companyGroup;

    @UiField
    HTMLPanel phonesContainer;

    @UiField
    HTMLPanel emailsContainer;

    @Inject
    @UiField ( provided = true )
    CategoryButtonSelector companyCategory;

    @UiField
    HTMLPanel tableContainer;

    @Inject
    @UiField
    Lang lang;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onChangeCompanyName();
            }
        }
    };

    AbstractCompanyEditActivity activity;

    private static CompanyViewUiBinder2 ourUiBinder = GWT.create(CompanyViewUiBinder2.class);
    interface CompanyViewUiBinder2 extends UiBinder<HTMLPanel, CompanyEditView> {}
}