package ru.protei.portal.ui.common.client.widget.privilege.list;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.widget.optionlist.base.ModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель списка привилегий
 */
public abstract class PrivilegeModel implements Activity {
    
    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        list = new ArrayList<>( event.profile.getPrivileges() );
        notifySubscribers();
    }

    public void subscribe( ModelList< En_Privilege > list ) {
        subscribers.add( list );
        list.fillOptions( this.list );
    }

    private void notifySubscribers() {
        for ( ModelList< En_Privilege > list : subscribers ) {
            list.fillOptions( this.list );
        }
    }

    private List< En_Privilege > list = new ArrayList<>();
    private List< ModelList< En_Privilege > > subscribers = new ArrayList<>();
}
