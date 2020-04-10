package ru.protei.portal.ui.role.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.RoleEvents;
import ru.protei.portal.ui.common.client.lang.En_PrivilegeEntityLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Активность превью роли
 */
public abstract class RolePreviewActivity
        implements Activity,
        AbstractRolePreviewActivity
{

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onShow( RoleEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.role );
    }

    private void fillView( UserRole value ) {
        view.setName( value.getCode() );
        view.setDescription( value.getInfo() );
        view.setPrivileges( value.getPrivileges().stream()
                .filter( privilege -> privilege.getAction() != null )
                .sorted( Comparator.comparingInt( En_Privilege::getOrder ) )
                .collect( Collectors.groupingBy( En_Privilege::getEntity, Collectors.mapping( En_Privilege::getActionShortName, Collectors.joining() ) ) )
                .entrySet().stream().map( entry -> entityLang.getName( entry.getKey() ) + ":" + entry.getValue() ).collect( Collectors.joining(", ") ) );
    }

    @Inject
    Lang lang;

    @Inject
    En_PrivilegeEntityLang entityLang;

    @Inject
    AbstractRolePreviewView view;

    private AppEvents.InitDetails initDetails;
}
