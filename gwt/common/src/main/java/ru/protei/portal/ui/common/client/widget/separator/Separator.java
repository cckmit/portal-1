package ru.protei.portal.ui.common.client.widget.separator;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Виджет разделителей страниц в таблице с бесконечной прокруткой
 */
public class Separator implements InfiniteTableWidget.SeparatorProvider {
    @Override
    public void fillSeparatorValue( Element element, int page, InfiniteTableWidget table ) {
        element.setInnerText( lang.separatorText( page+1, table.getPageCount() ) );
        element.addClassName( "separator" );
    }

    @Inject
    Lang lang;
}
