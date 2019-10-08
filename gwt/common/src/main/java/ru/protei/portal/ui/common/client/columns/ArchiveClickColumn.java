package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.function.Function;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

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
        lock.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.ARCHIVE);
        setMutableAttributes(archivedCheckFunction.apply(value));
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

    public void setArchiveHandler(ArchiveHandler<T> archiveHandler) {
        setActionHandler(archiveHandler::onArchiveClicked);
    }

    public void setArchivedCheckFunction(Function<T, Boolean> archivedCheckFunction) {
        this.archivedCheckFunction = archivedCheckFunction;
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
            lock.replaceClassName("fa-lg fa fa-archive", "fa-lg fa fa-history");
            lock.setTitle(lang.buttonFromArchive());
        } else {
            lock.removeClassName("archive-lock");
            lock.replaceClassName("fa-lg fa fa-history", "fa-lg fa fa-archive");
            lock.setTitle(lang.buttonToArchive());
        }
    }

    @Inject
    PolicyService policyService;

    Lang lang;

    private AnchorElement lock;
    private En_Privilege privilege;
    private Function<T, Boolean> archivedCheckFunction;
}
