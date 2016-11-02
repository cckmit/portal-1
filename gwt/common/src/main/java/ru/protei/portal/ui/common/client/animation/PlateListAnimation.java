package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Анимация диалогового окна
 */
public class PlateListAnimation {

    public void showPreview( IsWidget preview, IsWidget  previewContainer ) {
        if ( lastExpanded != null ) {
            closePreview( lastExpanded );
        }

        if ( lastExpanded == preview ) {
            lastExpanded = null;
            return;
        }

        lastExpanded = preview;
        preview.asWidget().addStyleName(EXPANDED_DETAILS_STYLE);
    }

    private void closePreview( IsWidget preview ) {
        preview.asWidget().removeStyleName(EXPANDED_DETAILS_STYLE);
    }

    private IsWidget lastExpanded = null;

    private static final String EXPANDED_DETAILS_STYLE = "plate-list-expanded";
}
