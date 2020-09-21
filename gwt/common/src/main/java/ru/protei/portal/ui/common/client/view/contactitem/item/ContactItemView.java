package ru.protei.portal.ui.common.client.view.contactitem.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.ui.common.client.activity.contactitem.AbstractContactItemActivity;
import ru.protei.portal.ui.common.client.activity.contactitem.AbstractContactItemView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.contacttype.ContactTypeButtonSelector;

import java.util.List;

/**
 * Представление элемента
 */
public class ContactItemView extends Composite implements AbstractContactItemView {

    @Inject
    public void onInit(){
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractContactItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasText value() {
        return value;
    }

    @Override
    public HasValue<En_ContactItemType> type() {
        return type;
    }

    @Override
    public HasVisibility typeVisibility() {
        return type;
    }


    @Override
    public void fillTypeOptions(List<En_ContactItemType> options) {
        type.fillOptions(options);
    }

    @Override
    public void focused(){
        value.setFocus(true);
    }

    @UiHandler( "value" )
    public void onChangeInputField( KeyUpEvent event ) {
        activity.onChangeValue(this);
    }

    @UiHandler( "type" )
    public void onChangeTypeField( ValueChangeEvent<En_ContactItemType> event ) {
        activity.onChangeType(this);
    }


    @UiField
    TextBox value;

    @Inject
    @UiField(provided = true)
    ContactTypeButtonSelector type;

    @Inject
    @UiField
    Lang lang;

    AbstractContactItemActivity activity;

    private static ValueCommentItemViewUiBinder ourUiBinder = GWT.create(ValueCommentItemViewUiBinder.class);
    interface ValueCommentItemViewUiBinder extends UiBinder<HTMLPanel, ContactItemView> {}
}
