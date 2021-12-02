package ru.protei.portal.ui.delivery.client.view.delivery.module.state;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.ModuleStateLang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.page.AbstractChangeStateHandler;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public class ModuleStatesPopup extends BasePopupView {

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
    }

    @Override
    protected UIObject getPositionRoot() {
        return root;
    }

    public void fillOptions(List<CaseState> states) {
        for (CaseState state : emptyIfNull(states)){

            Element li = DOM.createElement( "li" );
            li.addClassName( "kit-menu-item" );

            Element a = DOM.createAnchor();
            Element div = DOM.createDiv();
            div.addClassName( "kit-menu-item-info" );
            a.appendChild( div );

            Element icon = DOM.createElement( "i" );
            icon.addClassName( "kit-icon module-icon fas fa-circle selector m-r-5" );
            icon.getStyle().setColor( state.getColor() );
            div.appendChild( icon );

            Element span = DOM.createSpan();
            span.addClassName( "kit-text module-text" );
            span.setInnerText( getStateName(state) );
            div.appendChild( span );

            li.appendChild( a );
            a.setId(DEBUG_ID_PREFIX + DebugIds.DELIVERY.KIT.MODULE.CHANGE_STATE_POPUP + state.getState());
            root.getElement().appendChild(li);

            DOM.sinkEvents( a, Event.ONCLICK );
            DOM.setEventListener( a, (event) -> {
                if ( event.getTypeInt() != Event.ONCLICK ) {
                    return;
                }

                event.preventDefault();
                activity.onModulesStateChangeClicked(state);
                hide();
            });
        }
    }

    public String getStateName(CaseState state) {
        return moduleStateLang.getStateName(state);
    }

    @UiField
    Lang lang;
    @Inject
    ModuleStateLang moduleStateLang;
    @UiField
    HTMLPanel root;

    AbstractChangeStateHandler activity;

    public void addChangeStateHandler(AbstractChangeStateHandler activity) {
        this.activity = activity;
    }

    interface ModuleStatesPopupUiBinder extends UiBinder<HTMLPanel, ModuleStatesPopup> {}
    private static ModuleStatesPopupUiBinder ourUiBinder = GWT.create(ModuleStatesPopupUiBinder.class);
}
