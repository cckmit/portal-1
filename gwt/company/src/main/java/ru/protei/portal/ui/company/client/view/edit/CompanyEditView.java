package ru.protei.portal.ui.company.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.client.service.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextArea;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditActivity;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditView;
import ru.protei.portal.ui.company.client.widget.group.inputselector.GroupInputSelector;

/**
 * Вид создания и редактирования компании
 */
public class CompanyEditView extends Composite implements AbstractCompanyEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractCompanyEditActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasText companyName() {
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
    public HasText actualAddress() {
        return actualAddress;
    }

    @Override
    public HasValidable actualAddressValidator() {
        return actualAddress;
    }

    @Override
    public HasText legalAddress() {
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

    @UiHandler( "companyName" )
    public void onKeyUp( KeyUpEvent keyUpEvent ) {
        verifiableIcon.setClassName(NameStatus.UNDEFINED.getStyle());
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiHandler( "companyGroup" )
    public void onChangeCompanyGroup( ValueChangeEvent< CompanyGroup > event) {
        if(event.getValue() == null && !companyGroup.inputText().getText().trim().isEmpty())
            createCompanyGroupBtn.removeStyleName("inactive"); // позволяем создать
        else
            createCompanyGroupBtn.addStyleName("inactive"); // запрещаем создавать
    }

    @UiHandler( "createCompanyGroupBtn" )
    public void onCreateCompanyGroup( ClickEvent event ) {
        if(!createCompanyGroupBtn.getElement().hasClassName("inactive")) {
            companyGroup.removeOption(tempCompanyGroup.getName()); // delete previous temp company group

            String newCompanyName = companyGroup.inputText().getText().trim();
            tempCompanyGroup.setName(newCompanyName);
            companyGroup.addAndSetOption(tempCompanyGroup);

            createCompanyGroupBtn.addStyleName("inactive");
        }
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onChangeCompanyName();
            }
        }
    };


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

    @UiField
    Button createCompanyGroupBtn;

    @Inject
    @UiField( provided = true )
    GroupInputSelector companyGroup;


    CompanyGroup tempCompanyGroup = new CompanyGroup();
    AbstractCompanyEditActivity activity;

    private static CompanyViewUiBinder2 ourUiBinder = GWT.create(CompanyViewUiBinder2.class);
    interface CompanyViewUiBinder2 extends UiBinder<HTMLPanel, CompanyEditView> {}
}