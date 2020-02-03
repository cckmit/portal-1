package ru.protei.portal.ui.product.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.service.ProductService;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Активность карточки просмотра продукта
 */
public abstract class ProductPreviewActivity implements AbstractProductPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow(ProductEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget(event.isShouldWrap));

        loadDetails(event.productId);
        view.showFullScreen(false);
    }

    @Event
    public void onShow(ProductEvents.ShowFullScreen event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.PRODUCT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget(true));

        loadDetails(event.productId);
        view.showFullScreen(true);
    }

    @Override
    public void onFullScreenClicked() {
        fireEvent(new ProductEvents.ShowFullScreen(productId));
    }

    @Override
    public void onBackButtonClicked() {
        fireEvent(new ProductEvents.Show());
    }

    private void loadDetails(Long productId) {
        productService.getProduct(productId, new FluentCallback<DevUnit>().withSuccess(this::fillView));
    }

    private void fillView(DevUnit product) {
        this.productId = product.getId();
        view.setName(product.getName() + (CollectionUtils.isEmpty(product.getAliases()) ? "" : " (" + product.getAliases().stream().collect(Collectors.joining(", ")) + ")"));
        view.setTypeImage(product.getType() == null ? null : product.getType().getImgSrc());
        view.setInfo(product.getInfo());
        view.setDirection(product.getProductDirection() == null ? "" : product.getProductDirection().getName());
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
    AbstractProductPreviewView view;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    ProductControllerAsync productService;
    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;
    private Long productId;
}
