package ru.protei.portal.ui.role.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.role.client.activity.preview.AbstractRolePreviewActivity;
import ru.protei.portal.ui.role.client.activity.preview.AbstractRolePreviewView;

/**
 * Вид превью роли
 */
public class RolePreviewView extends Composite implements AbstractRolePreviewView {

    public RolePreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractRolePreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName( String value ) {
        this.name.setInnerText( value );
    }

    @Override
    public void setDescription( String value ) {
        this.description.setText( value );
    }

    @Inject
    @UiField
    Lang lang;
    @UiField
    HeadingElement name;
    @UiField
    Label description;

    AbstractRolePreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, RolePreviewView > { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}