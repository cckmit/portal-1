package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.ent.Refreshable;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class RefreshClickColumn<T> extends ClickColumn<T> {

    public interface RefreshHandler<T> extends AbstractColumnHandler<T> {
        void onRefreshClicked(T value);
    }

    @Inject
    public RefreshClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("refresh");
    }

    @Override
    public void fillColumnValue(Element cell, T value) {
        if (((Refreshable) value).isAllowedRefresh()) {
            AnchorElement a = DOM.createAnchor().cast();
            a.setHref("#");
            a.addClassName("fa fa-lg fa-refresh");
            a.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.REFRESH);
            cell.appendChild(a);
        }
    }

    public void setRefreshHandler(RefreshHandler<T> refreshHandler) {
        setActionHandler(refreshHandler::onRefreshClicked);
    }

    Lang lang;
    RefreshHandler<T> refreshHandler;
}
