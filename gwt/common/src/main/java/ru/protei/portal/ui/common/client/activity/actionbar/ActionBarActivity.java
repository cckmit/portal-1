package ru.protei.portal.ui.common.client.activity.actionbar;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.winter.web.common.client.activity.section.AbstractSectionItemActivity;
import ru.protei.winter.web.common.client.activity.section.AbstractSectionItemView;

import java.util.HashMap;
import java.util.Map;

/**
 * Активность в меню
 */
public abstract class ActionBarActivity
        implements Activity, AbstractSectionItemActivity {

    @Event
    public void onInit( ActionBarEvents.Init event ) {
        init = event;
    }

    @Event
    public void onShowSectionItem( ActionBarEvents.Add event ){
        addSection( event.header, event.icon, event.identity, event.debugId );
    }

    @Event
    public void onClearAllSection( ActionBarEvents.Clear event ) {
        init.parent.clear();
        itemViewToIdentity.clear();
    }

    @Event
    public void onSetButtonEnabled( ActionBarEvents.SetButtonEnabled event ) {
        itemViewToIdentity.forEach((view, identity) -> {
            if (identity.equals(event.identity)) {
               view.setEnabled(event.isEnabled);
            }
        });
    }

    @Override
    public void onSectionClicked( AbstractSectionItemView itemView ) {
        if ( itemViewToIdentity == null ) {
            return;
        }

        String identity = itemViewToIdentity.get( itemView );
        fireEvent(new ActionBarEvents.Clicked(identity));
    }

    private void addSection( String header, String icon, String identity, String debugId ) {
        AbstractSectionItemView itemView = factory.get();
        itemView.setActivity( this );
        itemView.setText( header );
        itemView.setEnsureDebugId( debugId );
        itemView.asWidget().addStyleName("btn m-r-15");
        itemView.addClickHandler();

        init.parent.add( itemView.asWidget() );
        itemViewToIdentity.put(itemView, identity);
    }


    @Inject
    Provider<AbstractSectionItemView> factory;

    ActionBarEvents.Init init;

    Map<AbstractSectionItemView, String> itemViewToIdentity = new HashMap< AbstractSectionItemView, String >(  );
}
