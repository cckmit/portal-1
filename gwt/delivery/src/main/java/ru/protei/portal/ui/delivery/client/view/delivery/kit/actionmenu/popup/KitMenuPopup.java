package ru.protei.portal.ui.delivery.client.view.delivery.kit.actionmenu.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.ModuleStateLang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.delivery.client.activity.actionmenu.AbstractKitMenuPopupActivity;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public class KitMenuPopup extends BasePopupView {

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
        ensureDebugIds();
    }

    @Override
    protected UIObject getPositionRoot() {
        return root;
    }

    public void setActivity(AbstractKitMenuPopupActivity activity){
        this.activity = activity;
    }

    @UiHandler("copy")
    public void onCopyClick(ClickEvent event) {
        event.preventDefault();
        activity.onCopyClick();
        hide();
    }

    @UiHandler("changeState")
    public void onChangeStateClick(MouseOverEvent event) {
        statesContainer.removeClassName("hide");
    }

    @UiHandler("changeState")
    public void onChangeStateClick(MouseOutEvent event) {
        statesContainer.addClassName("hide");
    }

    @UiHandler("edit")
    public void onEditClick(ClickEvent event) {
        event.preventDefault();
        activity.onEditClick();
        hide();
    }

    @UiHandler("remove")
    public void onRemoveClick(ClickEvent event) {
        event.preventDefault();
        activity.onRemoveClick();
        hide();
    }

    public void setChangeStateSubmenuItems(List<CaseState> states) {
        for (CaseState state : emptyIfNull(states)){

            Element li = DOM.createElement( "li" );
            li.addClassName( "kit-submenu-item" );

            Element a = DOM.createAnchor();

            Element icon = DOM.createElement( "i" );
            icon.addClassName( "kit-icon fas fa-circle selector m-r-5" );
            icon.getStyle().setColor( state.getColor() );
            a.appendChild( icon );

            Element span = DOM.createSpan();
            span.addClassName( "kit-text" );
            span.setInnerText( getStateName(state) );
            a.appendChild( span );

            li.appendChild( a );
            statesContainer.appendChild(li);

            DOM.sinkEvents( a, Event.ONCLICK );
            DOM.setEventListener( a, (event) -> {
                if ( event.getTypeInt() != Event.ONCLICK ) {
                    return;
                }

                event.preventDefault();
                activity.onChangeStateClick(state);
                hide();
            });
        }
    }

    public String getStateName(CaseState state) {
        return moduleStateLang.getStateName(state);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        copy.ensureDebugId(DebugIds.DELIVERY.KIT.ACTION_MENU_ITEM_COPY_BUTTON);
        changeState.ensureDebugId(DebugIds.DELIVERY.KIT.ACTION_MENU_ITEM_CHANGE_STATE_BUTTON);
        edit.ensureDebugId(DebugIds.DELIVERY.KIT.ACTION_MENU_ITEM_EDIT_BUTTON);
        remove.ensureDebugId(DebugIds.DELIVERY.KIT.ACTION_MENU_ITEM_REMOVE_BUTTON);
    }

    @UiField
    Lang lang;
    @Inject
    ModuleStateLang moduleStateLang;
    @UiField
    HTMLPanel root;

    @UiField
    UListElement statesContainer;

    @UiField
    Anchor copy;
    @UiField
    Anchor changeState;
    @UiField
    Anchor edit;
    @UiField
    Anchor remove;

    AbstractKitMenuPopupActivity activity;

    interface KitMenuPopupUiBinder extends UiBinder<HTMLPanel, KitMenuPopup> {}
    private static KitMenuPopupUiBinder ourUiBinder = GWT.create(KitMenuPopupUiBinder.class);
}
