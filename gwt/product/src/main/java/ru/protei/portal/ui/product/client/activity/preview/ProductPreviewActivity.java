package ru.protei.portal.ui.product.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

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
            fireEvent(new ErrorPageEvents.ShowForbidden());
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
        fireEvent(new ProductEvents.Show(true));
    }

    private void loadDetails(Long productId) {
        productService.getProduct(productId, new FluentCallback<DevUnit>().withSuccess(this::fillView));
    }

    private void fillView(DevUnit product) {
        this.productId = product.getId();
        view.setName(product.getName() + (isEmpty(product.getAliases()) ? "" : " (" + joining(product.getAliases(), ", ") + ")"));
        view.setTypeImage(product.getType() == null ? null : product.getType().getImgSrc());
        view.setDirection(product.getProductDirections() == null ? "" : joining(product.getProductDirections(), ", ", DevUnit::getName));
        view.setInternalDocLink(StringUtils.emptyIfNull(product.getInternalDocLink()));
        view.setExternalDocLink(StringUtils.emptyIfNull(product.getExternalDocLink()));

        view.setParents(emptyIfNull(product.getParents()).stream().collect(Collectors.toMap(DevUnit::getName, devUnit -> LinkUtils.makePreviewLink(DevUnit.class, devUnit.getId()))));
        view.setChildren(emptyIfNull(product.getChildren()).stream().collect(Collectors.toMap(DevUnit::getName, devUnit -> LinkUtils.makePreviewLink(DevUnit.class, devUnit.getId()))));

        view.parentsContainerVisibility().setVisible(!En_DevUnitType.COMPLEX.equals(product.getType()));

        List<String> list = new ArrayList<>();
        list.add(product.getConfiguration());
        list.add(product.getCdrDescription());
        list.add(product.getHistoryVersion());
        list.add(product.getInfo());
        view.setConfiguration(list.get(0));
        view.setCdrDescription(list.get(1));
        view.setHistoryVersion(list.get(2));
        view.setInfo(list.get(3));
        textRenderController.render(En_TextMarkup.MARKDOWN, list, new FluentCallback<List<String>>()
                .withError(throwable -> {
                    view.setConfiguration(list.get(0));
                    view.setCdrDescription(list.get(1));
                    view.setHistoryVersion(list.get(2));
                    view.setInfo(list.get(3));
                })
                .withSuccess(converted -> {
                    view.setConfiguration(converted.get(0));
                    view.setCdrDescription(converted.get(1));
                    view.setHistoryVersion(converted.get(2));
                    view.setInfo(converted.get(3));
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
