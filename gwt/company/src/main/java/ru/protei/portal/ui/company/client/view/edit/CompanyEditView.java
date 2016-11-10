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
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.StaticTextColumn;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.common.ContactColumnBuilder;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextArea;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditActivity;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditView;
import ru.protei.portal.ui.company.client.view.edit.columns.EditColumn;
import ru.protei.portal.ui.company.client.widget.category.buttonselector.CategoryButtonSelector;
import ru.protei.portal.ui.company.client.widget.group.buttonselector.GroupButtonSelector;

import java.util.List;
import java.util.logging.Logger;

/**
 * Вид создания и редактирования компании
 */
public class CompanyEditView extends Composite implements AbstractCompanyEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initContactTable();
    }

    @Override
    public void setActivity( AbstractCompanyEditActivity activity ) {
        this.activity = activity;
        editColumn.setHandler( activity );
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
    public void addContacts( List<Person> persons ) {
        persons.forEach( person -> {
            table.addRow( person );
        } );
        table.setVisible( true );
    }

    @Override
    public void clearContacts() {
        table.clearRows();
        table.setVisible( false );
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

    private void initContactTable() {

        editColumn = new EditColumn<>( lang );

        StaticTextColumn< Person > displayName = new StaticTextColumn<Person>( lang.contactFullName() ) {
            @Override
            public String getColumnValue( Person person ) {
                return person == null ? "" : person.getDisplayName();
            }
        };

        StaticTextColumn< Person > position = new StaticTextColumn<Person>( lang.contactPosition() ) {
            @Override
            public String getColumnValue( Person person ) {
                return person == null ? "" : person.getPosition();
            }
        };

        StaticTextColumn< Person > department = new StaticTextColumn<Person>( lang.department() ) {
            @Override
            public String getColumnValue( Person person ) {
                return person == null ? "" : person.getDepartment();
            }
        };

        StaticTextColumn< Person > phone = new StaticTextColumn<Person>( lang.phone() ) {
            @Override
            public String getColumnValue( Person person ) {
                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo());
                return ContactColumnBuilder.make().add( null, infoFacade.getWorkPhone() )
                        .add(null, infoFacade.getMobilePhone())
                        .add( null, infoFacade.getHomePhone()).toElement().getInnerText();
            }
        };

        StaticTextColumn< Person > email = new StaticTextColumn<Person>( lang.email() ) {
            @Override
            public String getColumnValue( Person person ) {
                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo());
                return ContactColumnBuilder.make().add( null, infoFacade.getEmail() )
                        .add( null, infoFacade.getEmail_own() ).toElement().getInnerText();
            }
        };

        table.addColumn( editColumn.header, editColumn.values );
        table.addColumn( displayName.header, displayName.values );
        table.addColumn( position.header, position.values );
        table.addColumn( department.header, department.values );
        table.addColumn( phone.header, phone.values );
        table.addColumn( email.header, email.values );

        table.setVisible( false );
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
    TableWidget table;

    @Inject
    @UiField
    Lang lang;

    EditColumn< Person > editColumn;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onChangeCompanyName();
            }
        }
    };

    AbstractCompanyEditActivity activity;

    private final static Logger log = Logger.getLogger( "ui" );

    private static CompanyViewUiBinder2 ourUiBinder = GWT.create(CompanyViewUiBinder2.class);
    interface CompanyViewUiBinder2 extends UiBinder<HTMLPanel, CompanyEditView> {}
}