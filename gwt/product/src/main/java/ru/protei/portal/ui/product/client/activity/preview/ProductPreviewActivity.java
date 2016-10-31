package ru.protei.portal.ui.product.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Активность карточки просмотра продукта
 */
public abstract class ProductPreviewActivity implements AbstractProductPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( ProductEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.product );
    }

    private void fillView( DevUnit product ) {
        view.setInfo( product.getInfo() );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProductPreviewView view;
}
