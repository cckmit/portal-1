package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

import java.util.Set;

/**
 * Мультиселектор продуктов
 */
public class DevUnitMultiSelector extends InputPopupMultiSelector<ProductShortView>
{

    @Inject
    public void init(ProductModel model, Lang lang) {
        this.model = model;
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        model.setUnitState( En_DevUnitState.ACTIVE );
        setItemRenderer(makeSelectorItemRenderer());
        setNullItem(() -> new ProductShortView( CrmConstants.Product.UNDEFINED, lang.productWithout(), 0 ));
    }

    public void setTypes(En_DevUnitType... enDevUnitTypes) {
        model.setUnitTypes(enDevUnitTypes);
    }

    public void setState(En_DevUnitState enDevUnitState) {
        model.setUnitState(enDevUnitState);
    }

    public void setDirectionIds(Set<Long> directionIds) {
        model.setDirectionIds(directionIds);
    }

    public void setImageVisible(boolean imageVisible) {
        isImageVisible = imageVisible;
    }

    private SelectorItemRenderer<ProductShortView> makeSelectorItemRenderer() {
        return new SelectorItemRenderer<ProductShortView>() {
            @Override
            public String getElementName(ProductShortView productShortView) {
                return "[" + lang.getName(productShortView.getType()) + "] " + makeName(productShortView);
            }

            @Override
            public String getElementHtml(ProductShortView productShortView) {
                HTMLPanel root = new HTMLPanel("");
                if (isImageVisible && productShortView.getType() != null) {
                    root.add(makeImage(productShortView.getType()));
                }
                root.add(new InlineLabel(makeName(productShortView)));
                return root.toString();
            }
        };
    }

    private String makeName(ProductShortView productShortView) {
        return productShortView.getName() + (HelperFunc.isEmpty(productShortView.getAliases()) ? "" : " (" + productShortView.getAliases() + ")");
    }

    private Widget makeImage(En_DevUnitType type) {
        Image image = new Image(type.getImgSrc());
        image.setAltText(lang.getName(type));
        image.setStyleName("dev-unit-selector-image");
        return image;
    }

    private boolean isImageVisible;

    private ProductModel model;

    @Inject
    En_DevUnitTypeLang lang;
}
