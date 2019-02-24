package ru.protei.portal.ui.product.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.MarkdownClient;
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
        productName.setInnerText(name);
    }

    @Override
    public void setType(String type) {
        productType.setInnerText(type);
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
        this.configuration.setInnerHTML(markdownClient.plain2escaped2markdown(value));
    }

    @Override
    public void setHistoryVersion(String value ) {
        this.historyVersion.setInnerHTML(markdownClient.plain2escaped2markdown(value));
    }

    @Override
    public void setCdrDescription(String value ) {
        this.cdrDescription.setInnerHTML(markdownClient.plain2escaped2markdown(value));
    }

    @Override
    public Widget asWidget(boolean isForTableView) {
        if(isForTableView){
            rootWrapper.addStyleName("preview-wrapper");
        }else {
            rootWrapper.removeStyleName("preview-wrapper");
        }

        productNameBlock.setVisible(isForTableView);
        return asWidget();
    }

    @UiField
    Lang lang;
    @UiField
    SpanElement info;
    @UiField
    HTMLPanel rootWrapper;
    @UiField
    SpanElement productName;
    @UiField
    SpanElement productType;
    @UiField
    HTMLPanel productNameBlock;
    @UiField
    SpanElement wikiLink;
    @UiField
    DivElement configuration;
    @UiField
    DivElement historyVersion;
    @UiField
    DivElement cdrDescription;

    @Inject
    FixedPositioner positioner;
    @Inject
    MarkdownClient markdownClient;

    AbstractProductPreviewActivity activity;

    private static ProductPreviewViewUiBinder ourUiBinder = GWT.create (ProductPreviewViewUiBinder.class);
    interface ProductPreviewViewUiBinder extends UiBinder<HTMLPanel, ProductPreviewView> {}
}