package ru.protei.portal.ui.product.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        List<String> list = new ArrayList<>();
        list.add(product.getConfiguration());
        list.add(product.getCdrDescription());
        list.add(product.getHistoryVersion());
        view.setConfiguration(list.get(0));
        view.setCdrDescription(list.get(1));
        view.setHistoryVersion(list.get(2));
        textRenderController.render(En_TextMarkup.MARKDOWN, list, new FluentCallback<List<String>>()
                .withError(throwable -> {
                    view.setConfiguration(list.get(0));
                    view.setCdrDescription(list.get(1));
                    view.setHistoryVersion(list.get(2));
                })
                .withSuccess(converted -> {
                    view.setConfiguration(converted.get(0));
                    view.setCdrDescription(converted.get(1));
                    view.setHistoryVersion(converted.get(2));
                })
        );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProductPreviewView view;
    @Inject
    En_DevUnitTypeLang typeLang;
    @Inject
    TextRenderControllerAsync textRenderController;
}
