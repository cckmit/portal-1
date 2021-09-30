package ru.protei.portal.ui.delivery.client.view.card.infoComment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

public class CardNoteCommentView extends Composite  {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    public void setNote( String value ) {
        noteRO.setInnerHTML(value);
    }

    public void setComment( String value ) {
        commentRO.setInnerHTML(value);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        noteRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CARD.NOTE);
        commentRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CARD.COMMENT);
    }

    @UiField
    Lang lang;
    @UiField
    DivElement noteRO;
    @UiField
    DivElement commentRO;

    interface WidgetUiBinder extends UiBinder<HTMLPanel, CardNoteCommentView> { }
    private static WidgetUiBinder ourUiBinder = GWT.create( WidgetUiBinder.class );
}