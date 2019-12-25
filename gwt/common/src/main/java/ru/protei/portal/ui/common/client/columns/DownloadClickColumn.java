package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class DownloadClickColumn<T> extends ClickColumn<T> {

    public interface DownloadHandler<T> extends AbstractColumnHandler<T> {
        void onDownloadClicked(T value);
    }

    @Inject
    public DownloadClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected String getColumnClassName() {
        return "download";
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {}

    @Override
    public void fillColumnValue(Element cell, T value) {
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref("#");
        if ( imageUrl == null ) {
            a.addClassName("fa fa-lg fa-cloud-download-alt");
        } else {
            ImageElement img = DOM.createImg().cast();
            img.setSrc(imageUrl);
            img.setHeight(40);
            a.appendChild(img);
        }
        a.setTitle(lang.download());
        a.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.DOWNLOAD);
        if (enabledPredicate == null || enabledPredicate.isEnabled(value)) {
            a.removeClassName("link-disabled");
        } else {
            a.addClassName("link-disabled");
        }
        cell.appendChild(a);
    }

    public void setDownloadHandler(DownloadHandler<T> downloadHandler) {
        setActionHandler(downloadHandler::onDownloadClicked);
    }

    public void setDownloadCustomImage(String url) {
        this.imageUrl = url;
    }

    private Lang lang;
    private String imageUrl;
}
