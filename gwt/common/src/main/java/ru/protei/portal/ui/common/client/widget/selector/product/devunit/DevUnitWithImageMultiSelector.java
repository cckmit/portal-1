package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

public class DevUnitWithImageMultiSelector extends DevUnitMultiSelector {

    @Inject
    public void init(ProductModel model, Lang lang) {
        super.init(model, lang);
        setItemRenderer(makeSelectorItemRenderer());
    }

    @Override
    protected SelectorItem<ProductShortView> makeSelectorItem(ProductShortView element, String elementHtml, String name) {
        PopupSelectableItem<ProductShortView> item = new PopupSelectableItem<>();
        item.setElementHtml(elementHtml);
        item.setSelected(isSelected(element));
        item.setTitle(name);
        return item;
    }

    private SelectorItemRenderer<ProductShortView> makeSelectorItemRenderer() {
        return new SelectorItemRenderer<ProductShortView>() {
            @Override
            public String getElementName(ProductShortView productShortView) {
                return (productShortView.getType() != null ? "[" + lang.getName(productShortView.getType()) + "] " : "")
                        + makeName(productShortView);
            }

            @Override
            public String getElementHtml(ProductShortView productShortView) {
                HTMLPanel root = new HTMLPanel("");
                if (productShortView.getType() != null) {
                    root.add(makeImage(productShortView.getType()));
                }
                root.add(new InlineLabel(makeName(productShortView)));
                return root.toString();
            }
        };
    }

    private String makeName(ProductShortView productShortView) {
        return productShortView.getName()
                + (HelperFunc.isEmpty(productShortView.getAliases()) ? "" : " (" + productShortView.getAliases() + ")");
    }

    private Widget makeImage(En_DevUnitType type) {
        Image image = new Image(type.getImgSrc());
        image.setAltText(lang.getName(type));
        image.setStyleName("dev-unit-selector-image");
        return image;
    }

    @Inject
    En_DevUnitTypeLang lang;
}
