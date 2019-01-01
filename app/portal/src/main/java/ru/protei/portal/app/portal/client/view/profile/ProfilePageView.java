package ru.protei.portal.app.portal.client.view.profile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.subscription.list.SubscriptionList;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.app.portal.client.activity.profile.AbstractProfilePageView;
import ru.protei.portal.app.portal.client.activity.profile.AbstractProfilePageActivity;

import java.util.List;

/**
 * Вид превью контакта
 */
public class ProfilePageView extends Composite implements AbstractProfilePageView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    @Override
    public void setActivity(AbstractProfilePageActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<List<Subscription>> companySubscription() {
        return subscriptions;
    }

    @Override
    public void setName( String name ) {
        this.name.setText( name );
    }

    @Override
    public void setCompany( String value ) {
        this.company.setInnerText( value );
    }

    @Override
    public HasVisibility saveButtonVisibility() {
        return saveButton;
    }

    @Override
    public void setIcon( String iconSrc ) {
        this.icon.setSrc( iconSrc );
    }

    @Override
    public HasEnabled companySubscriptionEnabled() {
        return subscriptions;
    }

    @UiHandler( "saveButton" )
    public void onButtonClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSaveSubscriptionClicked();
        }
    }

    @Inject
    @UiField
    Lang lang;
    @Inject
    @UiField(provided = true)
    SubscriptionList subscriptions;
    @UiField
    InlineLabel name;
    @UiField
    Button saveButton;
    @UiField
    Element company;
    @UiField
    ImageElement icon;

    @Inject
    FixedPositioner positioner;

    AbstractProfilePageActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, ProfilePageView > { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}