package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка редактирования контакта.
 */
public class EditClickColumn< T > extends ru.protei.portal.ui.common.client.columns.ClickColumn< T > {

    public interface EditHandler< T > extends AbstractColumnHandler< T > {
        void onEditClicked( T value );
    }

    @Inject
    public EditClickColumn( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element element ) {
        element.addClassName( "edit" );
    }

    @Override
    public void fillColumnValue( Element cell, T value ) {
        cell.addClassName( "edit" );
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref( "#" );
        a.addClassName( "fa-2x ion-compose" );
        a.setTitle( lang.edit() );
        setEditEnabled( a );
        cell.appendChild( a );
    }

    public void setPrivilege( En_Privilege privilege ) {
        this.privilege = privilege;
    }

    public void setEditHandler( EditHandler< T > editHandler ) {
        setActionHandler(editHandler::onEditClicked);
    }

    private void setEditEnabled( AnchorElement a ) {

        if ( privilege == null ) {
            return;
        }

        if ( policyService.hasPrivilegeFor( privilege ) ) {
            a.removeClassName( "link-disabled" );
        } else {
            a.addClassName( "link-disabled" );
        }
    }

    @Inject
    PolicyService policyService;

    Lang lang;
    En_Privilege privilege;
}
