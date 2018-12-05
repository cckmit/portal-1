package ru.protei.portal.ui.project.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.state.RegionStateIconSelector;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewActivity;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewView;
import ru.protei.portal.ui.project.client.view.widget.team.TeamSelector;

import java.util.HashSet;
import java.util.Set;

/**
 * Вид превью проекта
 */
public class ProjectPreviewView extends Composite implements AbstractProjectPreviewView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        watchForScroll(false);
    }

    @Override
    public void watchForScroll(boolean isWatch) {
        if(isWatch)
            positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
        else
            positioner.ignore(this);
    }

    @Override
    public void setActivity( AbstractProjectPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setName( String name ) {
        this.projectName.setValue( name );
    }

    @Override
    public String getName() {
        return projectName.getValue();
    }

    @Override
    public void setInitiatorShortName(String value) {
        this.initiatorShortName.setInnerHTML( value );
    }

    @Override
    public void setCreationDate( String value ) {
        this.creationDate.setInnerText( value );
    }

    @Override
    public HasValue<En_RegionState> state() {
        return projectState;
    }

    @Override
    public HasValue<ProductDirectionInfo> direction() {
        return projectDirection;
    }

    @Override
    public HasValue<Set<PersonProjectMemberView>> team() {
        return team;
    }

    @Override
    public HasText details() {
        return details;
    }

    @Override
    public HasValue< EntityOption > region() {
        return projectRegion;
    }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return products;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<En_CustomerType> customerType() {
        return customerType;
    }

    @Override
    public void showFullScreen( boolean value ) {
        this.fullScreenBtn.setVisible( !value );
        if ( value ) {
            this.preview.addStyleName( "col-md-12 col-lg-6" );
        } else {
            this.preview.removeStyleName( "col-md-12 col-lg-6" );
        }
    }

    @Override
    public HasVisibility removeBtnVisibility() {
        return removeBtn;
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @UiHandler( "fullScreenBtn" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiHandler( "removeBtn" )
    public void onRemoveBtnClicked (ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onRemoveClicked();
        }
    }

    @UiHandler( {"projectName", "details"} )
    public void onNameChanged( KeyUpEvent event ) {
        fireProjectChanged();
    }

    @UiHandler( "projectDirection" )
    public void onDirectionChanged( ValueChangeEvent<ProductDirectionInfo> event ) {
        fireProjectChanged();
    }

    @UiHandler( "team" )
    public void onDeployManagersChanged( ValueChangeEvent<Set<PersonProjectMemberView>> value ) {
        fireProjectChanged();
    }

    @UiHandler( "projectState" )
    public void onStateChanged( ValueChangeEvent<En_RegionState> event ) {
        fireProjectChanged();
    }

    @UiHandler( {"projectRegion", "company"} )
    public void onRegionOrCompanyChanged( ValueChangeEvent<EntityOption> event ) {
        fireProjectChanged();
    }

    @UiHandler( "products" )
    public void onProductsChanged( ValueChangeEvent<Set<ProductShortView>> event ) {
        fireProjectChanged();
    }

    @UiHandler( "customerType" )
    public void onCustomerTypeChanged( ValueChangeEvent<En_CustomerType> event ) {
        fireProjectChanged();
    }

    private void fireProjectChanged() {
        projectChanged.cancel();
        projectChanged.schedule( 500 );
    }

    private Timer projectChanged = new Timer() {
        @Override
        public void run() {
            activity.onProjectChanged();
        }
    };

    @UiField
    HTMLPanel preview;

    @UiField
    Anchor fullScreenBtn;

    @UiField
    Anchor removeBtn;

    @UiField
    Element creationDate;

    @Inject
    @UiField(provided = true)
    TeamSelector team;

    @UiField
    TextArea details;

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel commentsContainer;

    @UiField
    TextBox projectName;

    @Inject
    @UiField( provided = true )
    ProductDirectionButtonSelector projectDirection;

    @Inject
    @UiField( provided = true )
    RegionStateIconSelector projectState;

    @Inject
    @UiField( provided = true )
    RegionButtonSelector projectRegion;

    @Inject
    FixedPositioner positioner;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector products;

    @Inject
    @UiField(provided = true)
    CustomerTypeSelector customerType;
    @UiField
    Element initiatorShortName;

    AbstractProjectPreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, ProjectPreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}