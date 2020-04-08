package ru.protei.portal.ui.role.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PrivilegeAction;
import ru.protei.portal.core.model.dict.En_PrivilegeEntity;
import ru.protei.portal.ui.common.client.lang.En_PrivilegeEntityLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.role.client.activity.preview.AbstractRolePreviewActivity;
import ru.protei.portal.ui.role.client.activity.preview.AbstractRolePreviewView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public void setPrivileges( String privileges ) {
        this.privileges.setText( privileges );

    }

    @Inject
    @UiField
    Lang lang;
    @UiField
    HeadingElement name;
    @UiField
    Label description;
    @UiField
    Label privileges;

    AbstractRolePreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, RolePreviewView > { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}