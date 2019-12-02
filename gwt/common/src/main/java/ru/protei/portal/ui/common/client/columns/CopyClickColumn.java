package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class CopyClickColumn<T> extends ClickColumn<T> {

    public interface CopyHandler<T> extends AbstractColumnHandler<T> {
        void onCopyClicked(T value);
    }

    @Inject
    public CopyClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected String getColumnClassName() {
        return "copy";
    }

    @Override
    protected void fillColumnHeader(Element element) {}

    @Override
    public void fillColumnValue(Element cell, T value) {
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref("#");
        a.addClassName("far fa-lg fa-copy");
        a.setTitle(lang.buttonCopy());
        a.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.COPY);
        if (enabledPredicate == null || enabledPredicate.isEnabled(value)) {
            a.removeClassName("link-disabled");
        } else {
            a.addClassName("link-disabled");
        }
        cell.appendChild(a);
    }

    public void setCopyHandler(CopyHandler<T> copyHandler) {
        setActionHandler(copyHandler::onCopyClicked);
    }

    private final Lang lang;
}
