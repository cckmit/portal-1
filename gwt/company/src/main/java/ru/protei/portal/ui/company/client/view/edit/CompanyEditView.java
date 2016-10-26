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
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.client.service.NameStatus;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.ValueCommentDataList;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.list.AutoAddVCList;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValEvent;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditActivity;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditView;
import ru.protei.portal.ui.company.client.widget.group.inputSelector.GroupInputSelector;

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
    public void setCompanyNameStatus(NameStatus status) {
        verifiableIcon.setClassName(status.getStyle());
    }

    @Override
    public HasText actualAddress() {
        return actualAddress;
    }

    @Override
    public HasText legalAddress() {
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
    public ValueCommentDataList phoneDataList() {
        return phones;
    }

    @Override
    public ValueCommentDataList emailDataList() {
        return emails;
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
    public void onChangeCompanyGroup(SelectorChangeValEvent event){
        if(event.getValue() != null && !event.getValue().trim().isEmpty()){
            createCompanyButton.getElement().removeClassName("inactive");
            tempNewCompanyGroup.setName(event.getValue());
        }
        else
            createCompanyButton.getElement().addClassName("inactive");
    }

    @UiHandler( "createCompanyButton" )
    public void onCreateCompanyClicked( ClickEvent event ) {
        if ( tempNewCompanyGroup.getName() != null ) {
            companyGroup.setValue(tempNewCompanyGroup);
            companyGroup.close();
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
    TextBox companyName;

    @UiField
    Element verifiableIcon;

    @UiField
    TextArea actualAddress;

    @UiField
    TextArea legalAddress;

    @UiField
    TextArea comment;

    @UiField
    TextBox webSite;

    @UiField
    Button createCompanyButton;

    @Inject
    @UiField( provided = true )
    GroupInputSelector companyGroup;

    @Inject
    @UiField( provided = true )
    AutoAddVCList phones;

    @Inject
    @UiField( provided = true )
    AutoAddVCList emails;


    AbstractCompanyEditActivity activity;
    CompanyGroup tempNewCompanyGroup = new CompanyGroup();

    private static CompanyViewUiBinder2 ourUiBinder = GWT.create(CompanyViewUiBinder2.class);
    interface CompanyViewUiBinder2 extends UiBinder<HTMLPanel, CompanyEditView> {}
}