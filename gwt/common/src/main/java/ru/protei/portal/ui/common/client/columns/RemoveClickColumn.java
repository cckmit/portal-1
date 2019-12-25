package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class RemoveClickColumn< T > extends ClickColumn< T > {

    public interface RemoveHandler< T > extends AbstractColumnHandler< T > {
        void onRemoveClicked( T value );
    }

    @Inject
    public RemoveClickColumn( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected String getColumnClassName() {
        return "remove";
    }

    @Override
    protected void fillColumnHeader( Element element ) {}

    @Override
    public void fillColumnValue( Element cell, T value ) {
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref( "#" );
        a.addClassName( "far fa-trash-alt fa-lg" );
        a.setTitle( lang.remove() );
        a.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.REMOVE );
        if (enabledPredicate == null || enabledPredicate.isEnabled(value)) {
            a.removeClassName( "link-disabled" );
        } else {
            a.addClassName( "link-disabled" );
        }
        cell.appendChild( a );
    }

    public void setRemoveHandler( RemoveHandler< T > removeHandler ) {
        setActionHandler(removeHandler::onRemoveClicked);
    }

    private final Lang lang;
}
