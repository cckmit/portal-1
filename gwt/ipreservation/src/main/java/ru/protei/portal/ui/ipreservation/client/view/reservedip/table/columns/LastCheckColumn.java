package ru.protei.portal.ui.ipreservation.client.view.reservedip.table.columns;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Информация о последней проверке"
 */
public class LastCheckColumn extends ClickColumn<ReservedIp> {

    @Inject
    public LastCheckColumn(Lang lang) { this.lang = lang; }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("ip-last-check");
        columnHeader.setInnerText(lang.reservedIpOnlineStatus());
    }

    @Override
    public void fillColumnValue(Element cell, ReservedIp value) {
        cell.addClassName("ip-last-check");

        if ( value == null ) { return; }

        final String date = DateFormatter.formatDateTime(value.getLastActiveDate());
        cell.setInnerText( StringUtils.isBlank(date) ? "" : date );
    }

    private Lang lang;
}
