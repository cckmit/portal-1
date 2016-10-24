package ru.protei.portal.ui.company.client.animation;

import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;

/**
 * Анимация диалогового окна
 */
public class GridAnimation {

    public void showPreview( AbstractCompanyItemView preview ) {
        if ( lastExpanded != null ) {
            closePreview( lastExpanded );
        }

        lastExpanded = preview;
        preview.asWidget().addStyleName( EXPANDED_DETAILS_STYLE );
    }

    public void hidePreview( AbstractCompanyItemView preview ) {
        closePreview( preview );
    }

    private void closePreview( AbstractCompanyItemView preview ) {
        preview.asWidget().removeStyleName( EXPANDED_DETAILS_STYLE );
        lastExpanded = null;
    }

    private AbstractCompanyItemView lastExpanded = null;

    private static final String EXPANDED_DETAILS_STYLE = "list-group-expanded";
}
