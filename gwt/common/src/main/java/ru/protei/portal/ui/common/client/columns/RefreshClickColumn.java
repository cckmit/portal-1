package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.ent.Refreshable;
import ru.protei.portal.ui.common.client.lang.Lang;

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
                if (refreshHandler != null) {
                    refreshHandler.onRefreshClicked(value);
                }
            });
        }
    }

    public void setRefreshHandler(RefreshHandler<T> refreshHandler) {
        this.refreshHandler = refreshHandler;
    }

    Lang lang;
    RefreshHandler<T> refreshHandler;
}
