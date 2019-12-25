package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Created by bondarenko on 27.12.16.
 */
abstract public class AttachClickColumn< T extends CaseShortView> extends ru.protei.portal.ui.common.client.columns.ClickColumn< T > {

    public interface AttachHandler< T > extends AbstractColumnHandler< T > {
        void onAttachClicked(T value, IsWidget widget);
    }

    @Inject
    public AttachClickColumn(Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected String getColumnClassName() {
        return "attachment";
    }

    @Override
    protected void fillColumnHeader(Element element) {}

    @Override
    public void fillColumnValue( Element cell, T value ) {
        if( !value.isAttachmentExists() )
            return;

        Anchor a = new Anchor();
        a.setHref( "#" );
        a.setStyleName( "fa fa-lg fa-paperclip" );
        a.setTitle( lang.attachment() );
        a.getElement().setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.ATTACHMENT );
        cell.appendChild( a.getElement() );

        DOM.sinkEvents( a.getElement(), Event.ONCLICK );
        DOM.setEventListener( a.getElement(), (event) -> {
            if ( event.getTypeInt() != Event.ONCLICK ) {
                return;
            }

            com.google.gwt.dom.client.Element target = event.getEventTarget().cast();
            if ( !"a".equalsIgnoreCase( target.getNodeName() ) ) {
                return;
            }

            event.preventDefault();
            if ( attachHandler != null ) {
                attachHandler.onAttachClicked( value, a);
            }
        });
    }

    public void setAttachHandler( AttachHandler< T > attachHandler ) {
        this.attachHandler = attachHandler;
    }

    Lang lang;

    AttachHandler< T > attachHandler;
}
