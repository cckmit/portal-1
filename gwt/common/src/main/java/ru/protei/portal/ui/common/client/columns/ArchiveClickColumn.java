package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Колонка вынесения сущности в архив
 */
public class ArchiveClickColumn<T> extends ClickColumn<T> {

    public interface ArchiveHandler<T> extends AbstractColumnHandler<T> {
        void onArchiveClicked(T value);
    }

    public interface ArchiveFilter<T> {
        boolean isArchived(T value);
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
        if (archiveFilter != null && archiveFilter.isArchived(value)) {
            lock.addClassName("archive-lock");
            lock.replaceClassName("fa-lg fa fa-archive", "fa-lg fa fa-history");
            lock.setTitle(lang.buttonFromArchive());
        } else {
            lock.removeClassName("archive-lock");
            lock.replaceClassName("fa-lg fa fa-history", "fa-lg fa fa-archive");
            lock.setTitle(lang.buttonToArchive());
        }
        if (enabledPredicate == null || enabledPredicate.isEnabled(value)) {
            lock.removeClassName("link-disabled");
        } else {
            lock.addClassName("link-disabled");
        }
        cell.appendChild(lock);
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("edit");
    }

    public void setArchiveHandler(ArchiveHandler<T> archiveHandler) {
        setActionHandler(archiveHandler::onArchiveClicked);
    }

    public void setArchiveFilter(ArchiveFilter<T> archiveFilter) {
        this.archiveFilter = archiveFilter;
    }

    private final Lang lang;
    private AnchorElement lock;
    private ArchiveFilter<T> archiveFilter;
}
