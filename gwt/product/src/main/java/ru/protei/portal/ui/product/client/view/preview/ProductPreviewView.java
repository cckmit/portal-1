package ru.protei.portal.ui.product.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.product.client.activity.preview.AbstractProductPreviewActivity;
import ru.protei.portal.ui.product.client.activity.preview.AbstractProductPreviewView;

/**
 * Вид карточки просмотра продукта
 */
public class ProductPreviewView extends Composite implements AbstractProductPreviewView {

    @Inject
    public void onInit() { initWidget(ourUiBinder.createAndBindUi(this)); }

    @Override
    protected void onDetach() {
        super.onDetach();
        watchForScroll(false);
    }

    @Override
    public void setActivity(AbstractProductPreviewActivity activity) { this.activity = activity; }

    @Override
    public void watchForScroll(boolean isWatch) {
        if(isWatch)
            positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
        else
            positioner.ignore(this);
    }

    @Override
    public void setName(String name) {
        this.name.setInnerText(name);
    }

    @Override
    public void setType(String type) {
        typeLabel.setText(type);
    }

    @Override
    public void setInfo( String value ) {
        this.info.setInnerText(value);
    }

    @Override
    public void setWikiLink(String value) {
        this.wikiLink.setInnerText(value);
    }

    @Override
    public void setConfiguration(String value ) {
        this.configuration.setInnerHTML(value);
    }

    @Override
    public void setHistoryVersion(String value ) {
        this.historyVersion.setInnerHTML(value);
    }

    @Override
    public void setCdrDescription(String value ) {
        this.cdrDescription.setInnerHTML(value);
    }

    @Override
    public void showFullScreen(boolean isFullScreen) {
        fullScreen.setVisible(!isFullScreen);
        backButtonPanel.setVisible(isFullScreen);

        if (isFullScreen) {
            rootWrapper.addStyleName("issue-fullscreen col-md-12 m-t-10");
        } else {
            rootWrapper.setStyleName("preview");
        }
    }

    @Override
    public Widget asWidget(boolean isForTableView) {
        if(isForTableView){
            rootWrapper.addStyleName("preview-wrapper");
        }else {
            rootWrapper.removeStyleName("preview-wrapper");
        }

        nameBlock.setVisible(isForTableView);
        return asWidget();
    }

    @UiHandler("fullScreen")
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
    Anchor fullScreen;
    @UiField
    Lang lang;
    @UiField
    SpanElement info;
    @UiField
    HTMLPanel rootWrapper;
    @UiField
    SpanElement name;
    @UiField
    HTMLPanel nameBlock;
    @UiField
    SpanElement wikiLink;
    @UiField
    DivElement configuration;
    @UiField
    DivElement historyVersion;
    @UiField
    DivElement cdrDescription;
    @UiField
    Label typeLabel;
    @UiField
    HTMLPanel backButtonPanel;
    @UiField
    Button backButton;

    @Inject
    FixedPositioner positioner;

    AbstractProductPreviewActivity activity;

    private static ProductPreviewViewUiBinder ourUiBinder = GWT.create (ProductPreviewViewUiBinder.class);
    interface ProductPreviewViewUiBinder extends UiBinder<HTMLPanel, ProductPreviewView> {}
}