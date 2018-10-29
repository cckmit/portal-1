package ru.protei.portal.ui.product.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
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
        event.parent.add( view.asWidget(event.isShouldWrap) );

        fillView( event.product );
        view.watchForScroll( event.isWatchForScroll);
    }

    private void fillView( DevUnit product ) {
        view.setName(product.getName());
        view.setTypeImage(product.getType() == null ? null : product.getType().getImgSrc());
        view.setInfo( product.getInfo() );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProductPreviewView view;
    @Inject
    En_DevUnitTypeLang typeLang;
}
