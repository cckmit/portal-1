package ru.protei.portal.ui.product.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.StringUtils;
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
        view.setType(typeLang.getName(product.getType()));
        view.setInfo( product.getInfo() );
        view.setWikiLink(StringUtils.emptyIfNull(product.getWikiLink()));
        view.setConfiguration(StringUtils.emptyIfNull(product.getConfiguration()));
        view.setCdrDescription(StringUtils.emptyIfNull(product.getCdrDescription()));
        view.setHistoryVersion(StringUtils.emptyIfNull(product.getHistoryVersion()));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProductPreviewView view;
    @Inject
    En_DevUnitTypeLang typeLang;
}
