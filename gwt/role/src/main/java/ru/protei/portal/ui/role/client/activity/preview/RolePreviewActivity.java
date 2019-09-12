package ru.protei.portal.ui.role.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.RoleEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

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
    }


    @Inject
    Lang lang;
    @Inject
    AbstractRolePreviewView view;

    private AppEvents.InitDetails initDetails;
}
