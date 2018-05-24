package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Downloadable;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;

public class DownloadClickColumn<T> extends ClickColumn<T> {

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
        if (((Downloadable) value).isAllowedDownload()) {

            AnchorElement a = DOM.createAnchor().cast();
            a.setHref("#");
            a.addClassName("fa fa-lg fa-cloud-download");
            a.setTitle(lang.download());
            setDownloadEnabled(a);
            cell.appendChild(a);

            DOM.sinkEvents(a, Event.ONCLICK);
            DOM.setEventListener(a, (event) -> {
                if (event.getTypeInt() != Event.ONCLICK) {
                    return;
                }

                com.google.gwt.dom.client.Element target = event.getEventTarget().cast();
                if (!"a".equalsIgnoreCase(target.getNodeName())) {
                    return;
                }

                event.preventDefault();
                if (downloadHandler != null) {
                    downloadHandler.onDownloadClicked(value);
                }
            });
        }
    }

    public void setPrivilege(En_Privilege privilege) {
        this.privilege = privilege;
    }

    public void setRemoveHandler(DownloadHandler<T> downloadHandler) {
        this.downloadHandler = downloadHandler;
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

    Lang lang;
    En_Privilege privilege;
    DownloadHandler<T> downloadHandler;
}
