package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;

public class CopyClickColumn<T> extends ClickColumn<T> {

    public interface CopyHandler<T> extends AbstractColumnHandler<T> {
        void onCopyClicked(T value);
    }

    @Inject
    public CopyClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element element) {
        element.addClassName("copy");
    }

    @Override
    public void fillColumnValue(Element cell, T value) {
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref("#");
        a.addClassName("fa-1-5x fa fa-copy");
        a.setTitle(lang.buttonCopy());
        setCopyEnabled(a);
        cell.appendChild(a);
    }

    public void setPrivilege(En_Privilege privilege) {
        this.privilege = privilege;
    }

    public void setCopyHandler(CopyHandler<T> copyHandler) {
        setActionHandler(copyHandler::onCopyClicked);
    }

    private void setCopyEnabled(AnchorElement a) {

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
}