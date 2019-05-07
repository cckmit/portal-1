package ru.protei.portal.ui.product.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        DecoratedTabBar bar = new DecoratedTabBar();
        bar.addTab("foo");
        bar.addTab("bar");
        bar.addTab("baz");
        rootWrapper.add(bar);


        DecoratedTabPanel panel = new DecoratedTabPanel();
        panel.add(new Button("1"), "1");
        panel.add(new Button( "2"), "2");
        rootWrapper.add(panel);
    }

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
    public Widget asWidget(boolean isForTableView) {
        if(isForTableView){
            rootWrapper.addStyleName("preview-wrapper");
        }else {
            rootWrapper.removeStyleName("preview-wrapper");
        }

//        productNameBlock.setVisible(isForTableView);
        return asWidget();
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
    HeadingElement productName;
    @UiField
    AnchorElement wikiLink;
    @UiField
    DivElement configuration;
    @UiField
    DivElement historyVersion;
    @UiField
    DivElement cdrDescription;

    @Inject
    FixedPositioner positioner;

    AbstractProductPreviewActivity activity;

    private static ProductPreviewViewUiBinder ourUiBinder = GWT.create (ProductPreviewViewUiBinder.class);
    interface ProductPreviewViewUiBinder extends UiBinder<HTMLPanel, ProductPreviewView> {}
}