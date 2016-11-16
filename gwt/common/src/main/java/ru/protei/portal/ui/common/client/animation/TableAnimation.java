package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Анимация базовой таблицы с карточкой
 */
public class TableAnimation {

    public void showDetails() {
        tableContainer.removeStyleName( "col-xs-12" );
        tableContainer.addStyleName( "col-xs-8" );
        detailsContainer.removeStyleName( "no-width" );
        detailsContainer.addStyleName( "col-xs-4" );
    }

    public void closeDetails() {
        detailsContainer.clear();
        tableContainer.removeStyleName( "col-xs-8" );
        tableContainer.addStyleName( "col-xs-12" );
        detailsContainer.removeStyleName( "col-xs-4" );
        detailsContainer.addStyleName( "no-width" );
    }

    public void setContainers( HTMLPanel tableContainer, HTMLPanel detailsContainer) {
        this.tableContainer = tableContainer;
        this.detailsContainer = detailsContainer;
    }

    private HTMLPanel tableContainer;
    private HTMLPanel detailsContainer;
}
