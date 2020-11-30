package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.ColumnClassName;
import static ru.protei.portal.ui.common.client.common.UiConstants.Icons;

public class CancelClickColumn<T> extends ClickColumn<T> {

    public interface CancelHandler<T> extends AbstractColumnHandler<T> {
        void onCancelClicked(T value);
    }

    @Inject
    public CancelClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected String getColumnClassName() {
        return ColumnClassName.CANCEL;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {}

    @Override
    public void fillColumnValue(Element cell, T value) {
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref("#");
        a.setTitle(lang.buttonCancel());
        a.addClassName("fas fa-lg " + Icons.CANCEL);
        a.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.CANCEL);
        if (enabledPredicate == null || enabledPredicate.isEnabled(value)) {
            a.removeClassName( UiConstants.Styles.LINK_DISABLE );
        } else {
            a.addClassName( UiConstants.Styles.LINK_DISABLE );
        }
        cell.appendChild(a);
    }

    public void setCancelHandler(CancelHandler<T> cancelHandler) {
        setActionHandler(cancelHandler::onCancelClicked);
    }

    Lang lang;
}
