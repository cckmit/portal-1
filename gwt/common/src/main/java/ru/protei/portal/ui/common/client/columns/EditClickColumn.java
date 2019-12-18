package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Колонка редактирования контакта.
 */
public class EditClickColumn< T > extends ru.protei.portal.ui.common.client.columns.ClickColumn< T > {

    public interface EditHandler< T > extends AbstractColumnHandler< T > {
        void onEditClicked( T value );
    }

    @Inject
    public EditClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected String getColumnClassName() {
        return "edit";
    }

    @Override
    protected void fillColumnHeader( Element element ) {}

    @Override
    public void fillColumnValue( Element cell, T value ) {
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref( "#" );
        a.addClassName( "far fa-edit fa-lg" );
        a.setTitle( lang.edit() );
        a.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.EDIT );
        if (enabledPredicate == null || enabledPredicate.isEnabled(value)) {
            a.removeClassName( "link-disabled" );
        } else {
            a.addClassName( "link-disabled" );
        }
        cell.appendChild( a );
    }

    public void setEditHandler( EditHandler< T > editHandler ) {
        setActionHandler( editHandler::onEditClicked );
    }

    private final Lang lang;
}
