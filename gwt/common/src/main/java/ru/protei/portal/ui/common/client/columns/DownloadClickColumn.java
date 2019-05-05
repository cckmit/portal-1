package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Downloadable;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;

public class DownloadClickColumn<T extends Downloadable> extends ClickColumn<T> {


    public interface DownloadHandler<T> extends AbstractColumnHandler<T> {
        void onDownloadClicked(T value);
    }

    @Inject
    public DownloadClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("download");
    }

    @Override
    public void fillColumnValue(Element cell, T value) {
        if (!value.isAllowedDownload())
            return;

        AnchorElement a = DOM.createAnchor().cast();
        a.setHref("#");
        if ( imageUrl == null ) {
            a.addClassName("fa fa-lg fa-cloud-download");
        } else {
            ImageElement img = DOM.createImg().cast();
            img.setSrc(imageUrl);
            img.setHeight(40);
            a.appendChild(img);
        }
        a.setTitle(lang.download());
        setDownloadEnabled(a);
        cell.appendChild(a);
    }

    public void setPrivilege(En_Privilege privilege) {
        this.privilege = privilege;
    }

    public void setDownloadHandler(DownloadHandler<T> downloadHandler) {
        setActionHandler(downloadHandler::onDownloadClicked);
    }

    public void setDownloadCustomImage(String url) {
        this.imageUrl = url;
    }

    private void setDownloadEnabled(AnchorElement a) {
        if (privilege == null) {
            return;
        }
        if (policyService.hasPrivilegeFor(privilege)) {
            a.removeClassName("link-disabled");
        } else {
            a.addClassName("link-disabled");
        }
    }


    @Inject
    PolicyService policyService;

    private Lang lang;
    private En_Privilege privilege;
    private String imageUrl;
}
