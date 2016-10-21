package ru.protei.portal.ui.company.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyActivity;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyView;

/**
 * Created by bondarenko on 21.10.16.
 */
public class CompanyView  extends Composite implements AbstractCompanyView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractCompanyActivity activity ) {
        this.activity = activity;
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
        verifiableIcon.setClassName("icon-verifiable");
        timer.cancel();
        timer.schedule( 300 );
    }

    @Override
    public HasText companyName() {
        return companyName;
    }

    @Override
    public void setCompanyNameStatus(CompanyNameStatus status) {
        verifiableIcon.setClassName(status.getStyle());
    }

    public enum CompanyNameStatus{
        SUCCESS ("icon-success"),
        ERROR ("icon-error"),
        UNDEFINED ("");

        private final String style;
        CompanyNameStatus (String type) { this.style = type; }
        public String getStyle() { return style; }
    }

    @Override
    public String getActualAddress() {
        return actualAddress.getValue();
    }

    @Override
    public String getLegalAddress() {
        return legalAddress.getValue();
    }

    @Override
    public String getWebSite() {
        return webSite.getValue();
    }

    @Override
    public String getComment() {
        return comment.getValue();
    }


    @Override
    public void setActualAddress(String val) {
        actualAddress.setValue(val);
    }

    @Override
    public void setLegalAddress(String val) {
        legalAddress.setValue(val);
    }

    @Override
    public void setWebSite(String val) {
        webSite.setValue(val);
    }

    @Override
    public void setComment(String val) {
        comment.setValue(val);
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
    TextAreaElement actualAddress;

    @UiField
    TextAreaElement legalAddress;

    @UiField
    TextAreaElement comment;

    @UiField
    InputElement webSite;


    AbstractCompanyActivity activity;

    private static CompanyViewUiBinder2 ourUiBinder = GWT.create(CompanyViewUiBinder2.class);
    interface CompanyViewUiBinder2 extends UiBinder<HTMLPanel, CompanyView> {}
}