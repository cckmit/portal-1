package ru.protei.portal.ui.product.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.product.client.activity.preview.AbstractProductPreviewActivity;
import ru.protei.portal.ui.product.client.activity.preview.AbstractProductPreviewView;

/**
 * Вид карточки просмотра продукта
 */
public class ProductPreviewView extends Composite implements AbstractProductPreviewView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
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
        this.info.setText(value);
    }

    @Override
    public void setWikiLink(String value) {
        String href = value == null ? "#" : value;
        wikiLink.setInnerText(value);

        if ( !href.startsWith("http://") && !href.startsWith("htts://") ) {
            href = "http://" + href;
        }
        wikiLink.setHref(href);
    }

    @Override
    public void setConfiguration(String value ) {
        this.configuration.getElement().setInnerHTML(value);
    }

    @Override
    public void setHistoryVersion(String value ) {
        this.historyVersion.getElement().setInnerHTML(value);
    }

    @Override
    public void setCdrDescription(String value ) {
        this.cdrDescription.getElement().setInnerHTML(value);
    }

    @Override
    public void showFullScreen(boolean isFullScreen) {
        backButtonPanel.setVisible(isFullScreen);
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

    @UiField
    Lang lang;
    @UiField
    Label info;
    @UiField
    ImageElement typeImage;
    @UiField
    HTMLPanel rootWrapper;
    @UiField
    Anchor productName;
    @UiField
    AnchorElement wikiLink;
    @UiField
    HTMLPanel configuration;
    @UiField
    HTMLPanel historyVersion;
    @UiField
    HTMLPanel cdrDescription;
    @UiField
    HTMLPanel backButtonPanel;
    @UiField
    Button backButton;

    AbstractProductPreviewActivity activity;

    private static ProductPreviewViewUiBinder ourUiBinder = GWT.create (ProductPreviewViewUiBinder.class);
    interface ProductPreviewViewUiBinder extends UiBinder<HTMLPanel, ProductPreviewView> {}
}