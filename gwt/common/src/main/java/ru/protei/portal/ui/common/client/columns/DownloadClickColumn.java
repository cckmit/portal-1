package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Downloadable;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.function.Function;

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
        if (!value.isAllowedDownload()) {
            return;
        }

        isArchived = archivedCheckFunction == null ? false : archivedCheckFunction.apply(value);

        AnchorElement a = DOM.createAnchor().cast();
        a.setHref("#");
        a.addClassName("fa fa-lg fa-cloud-download");
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

    public void setArchivedCheckFunction(Function<T, Boolean> archivedCheckFunction) {
        this.archivedCheckFunction = archivedCheckFunction;
    }

    private void setDownloadEnabled(AnchorElement a) {
        if (privilege == null) {
            return;
        }

        if (policyService.hasPrivilegeFor(privilege) && !isArchived) {
            a.removeClassName("link-disabled");
        } else {
            a.addClassName("link-disabled");
        }
    }

    @Inject
    PolicyService policyService;

    Lang lang;
    En_Privilege privilege;

    private boolean isArchived;
    private Function<T, Boolean> archivedCheckFunction;
}
