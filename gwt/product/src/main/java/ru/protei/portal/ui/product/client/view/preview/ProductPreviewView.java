package ru.protei.portal.ui.product.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.product.client.activity.preview.AbstractProductPreviewActivity;
import ru.protei.portal.ui.product.client.activity.preview.AbstractProductPreviewView;

import java.util.Map;

/**
 * Вид карточки просмотра продукта
 */
public class ProductPreviewView extends Composite implements AbstractProductPreviewView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractProductPreviewActivity activity) { this.activity = activity; }

    @Override
    public void setName(String name) {
        this.productName.setText(name);
    }

    @Override
    public void setTypeImage(String image) {
        typeImage.setSrc(image);
    }

    @Override
    public void setInfo( String value ) {
        this.info.setInnerHTML(value);
    }

    @Override
    public void setDirection(String direction) {
        this.direction.setInnerText(direction);
    }

    @Override
    public void setInternalDocLink(String value) {
        String href = value == null ? "#" : value;
        internalDocLink.setInnerText(value);

        if ( !href.startsWith(CrmConstants.LinkStart.HTTP) && !href.startsWith(CrmConstants.LinkStart.HTTPS) ) {
            href = CrmConstants.LinkStart.HTTP + href;
        }
        internalDocLink.setHref(href);
    }

    @Override
    public void setExternalDocLink(String value) {
        String href = value == null ? "#" : value;
        externalDocLink.setInnerText(value);

        if ( !href.startsWith(CrmConstants.LinkStart.HTTP) && !href.startsWith(CrmConstants.LinkStart.HTTPS) ) {
            href = CrmConstants.LinkStart.HTTP + href;
        }
        externalDocLink.setHref(href);
    }

    @Override
    public HasVisibility parentsContainerVisibility() {
        return parentsContainer;
    }

    @Override
    public void setParents(Map<String, String> nameToLink) {
        addLinksToContainer(nameToLink, parents);
    }

    @Override
    public void showFullScreen(boolean isFullScreen) {
        backButtonPanel.setVisible(isFullScreen);
        rootWrapper.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
    }

    @Override
    public Widget asWidget(boolean isForTableView) {
        if(isForTableView){
            rootWrapper.addStyleName("preview-wrapper");
        }else {
            rootWrapper.removeStyleName("preview-wrapper");
        }

        return asWidget();
    }

    @UiHandler("productName")
    public void onFullScreenClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onFullScreenClicked();
        }
    }

    @UiHandler("backButton")
    public void onBackButtonClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onBackButtonClicked();
        }
    }

    private void addLinksToContainer(Map<String, String> nameToLink, HTMLPanel linksContainer) {
        linksContainer.getElement().removeAllChildren();

        for (Map.Entry<String, String> currEntry : nameToLink.entrySet()) {
            AnchorElement parent = AnchorElement.as(DOM.createAnchor());
            parent.setInnerText(currEntry.getKey());
            parent.setHref(currEntry.getValue());
            parent.setAttribute("target", "_blank");
            linksContainer.getElement().appendChild(parent);
        }
    }

    private void ensureDebugIds() {
        productName.ensureDebugId(DebugIds.PRODUCT_PREVIEW.NAME);
        internalDocLink.setId(DebugIds.PRODUCT_PREVIEW.INTERNAL_DOC_LINK);
        externalDocLink.setId(DebugIds.PRODUCT_PREVIEW.EXTERNAL_DOC_LINK);
        info.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PRODUCT_PREVIEW.DESCRIPTION);
        direction.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PRODUCT_PREVIEW.DIRECTION_LABEL);
        parents.ensureDebugId(DebugIds.PRODUCT_PREVIEW.PARENTS_CONTAINER);
        backButton.ensureDebugId(DebugIds.PRODUCT_PREVIEW.BACK_BUTTON);
    }

    @UiField
    Lang lang;
    @UiField
    SpanElement info;
    @UiField
    SpanElement direction;
    @UiField
    HTMLPanel parents;
    @UiField
    HTMLPanel parentsContainer;
    @UiField
    ImageElement typeImage;
    @UiField
    HTMLPanel rootWrapper;
    @UiField
    Anchor productName;
    @UiField
    AnchorElement internalDocLink;
    @UiField
    AnchorElement externalDocLink;
    @UiField
    HTMLPanel backButtonPanel;
    @UiField
    Button backButton;

    AbstractProductPreviewActivity activity;

    private static ProductPreviewViewUiBinder ourUiBinder = GWT.create (ProductPreviewViewUiBinder.class);
    interface ProductPreviewViewUiBinder extends UiBinder<HTMLPanel, ProductPreviewView> {}
}
