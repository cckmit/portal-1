package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Removable;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;

public class RemoveClickColumn< T > extends ClickColumn< T > {

    public interface RemoveHandler< T > extends AbstractColumnHandler< T > {
        void onRemoveClicked( T value );
    }

    @Inject
    public RemoveClickColumn( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element element ) {
        element.addClassName( "remove" );
    }

    @Override
    public void fillColumnValue( Element cell, T value ) {
        if ( ((Removable) value).isAllowedRemove() ) {
            AnchorElement a = DOM.createAnchor().cast();
            a.setHref( "#" );
            a.addClassName("far fa-trash-alt fa-lg text-danger");
            a.setTitle( lang.remove() );
            setRemoveEnabled( a );
            cell.appendChild( a );
        }
    }

    public void setPrivilege( En_Privilege privilege ) {
        this.privilege = privilege;
    }

    public void setRemoveHandler( RemoveHandler< T > removeHandler ) {
        setActionHandler(removeHandler::onRemoveClicked);
    }

    private void setRemoveEnabled( AnchorElement a ) {

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
