package ru.protei.portal.ui.company.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.company.client.animation.GridAnimation;

/**
 * Активность превью компании
 */
public abstract class CompanyPreviewActivity
        implements Activity,
        AbstractCompanyPreviewActivity
{
    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( CompanyEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.company );
    }

    private void fillView( Company value ) {

    }

    @Inject
    Lang lang;
    @Inject
    AbstractCompanyPreviewView view;
}
