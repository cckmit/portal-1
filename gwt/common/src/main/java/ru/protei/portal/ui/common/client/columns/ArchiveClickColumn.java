package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Archived;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка вынесения сущности в архив
 */
public class ArchiveClickColumn<T> extends ClickColumn<T> {
    public interface ArchiveHandler<T> extends AbstractColumnHandler<T> {
        void onArchiveClicked(T value);
    }

    @Inject
    public ArchiveClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    public void fillColumnValue(Element cell, T value) {
        this.lock = DOM.createAnchor().cast();
        lock.setHref("#");
        setMutableAttributes(((Archived) value).isArchived());
        setRemoveEnabled(lock);
        cell.appendChild(lock);
    }

    public void setPrivilege(En_Privilege privilege) {
        this.privilege = privilege;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("edit");
    }

    private void setRemoveEnabled(AnchorElement a) {
        if (privilege == null) {
            return;
        }

        if (policyService.hasPrivilegeFor(privilege)) {
            a.removeClassName("link-disabled");
        } else {
            a.addClassName("link-disabled");
        }
    }

    private void setMutableAttributes(boolean isArchived) {
        if (isArchived) {
            lock.addClassName("archive-lock");
            lock.addClassName("fa-2x fa fa-lock");
            lock.removeClassName("fa-2x fa fa-unlock-alt");
            lock.setTitle(lang.buttonFromArchive());
        } else {
            lock.removeClassName("archive-lock");
            lock.addClassName("fa-2x fa fa-unlock-alt");
            lock.removeClassName("fa-2x fa fa-lock");
            lock.setTitle(lang.buttonToArchive());
        }
    }

    public void setArchiveHandler(ArchiveHandler<T> archiveHandler) {
        setActionHandler(archiveHandler::onArchiveClicked);
    }

    @Inject
    PolicyService policyService;

    private AnchorElement lock;

    private final Lang lang;
    private En_Privilege privilege;
}
