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
import static ru.protei.portal.ui.common.client.common.UiConstants.ColumnClassName.CREATE_CARD;

/**
 * Колонка создания
 */
public class CreateCardClickColumn<T> extends ClickColumn<T> {

    public interface CreateCardClickHandler<T> extends AbstractColumnHandler<T> {
        void onClicked(T value);
    }

    @Inject
    public CreateCardClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected String getColumnClassName() {
        return CREATE_CARD;
    }

    @Override
    protected void fillColumnHeader(Element element) { }

    @Override
    public void fillColumnValue(Element cell, T value) {
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref("#");
        a.addClassName("fa fa-fw fa-plus-circle fa-lg");
        a.setTitle(lang.cardsCreating());
        a.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.CREATE_CARD);

        if (enabledPredicate == null || enabledPredicate.isEnabled(value)) {
            a.removeClassName("link-disabled");
        } else {
            a.addClassName("link-disabled");
        }

        cell.appendChild(a);
    }

    public void setCreateCardHandler(CreateCardClickHandler<T> clickHandler) {
        setActionHandler(clickHandler::onClicked);
    }

    private final Lang lang;
}
