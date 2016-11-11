package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Анимация базовой таблицы с карточкой
 */
public class TableAnimation {

    public void showDetails() {
        tableContainer.removeStyleName( "col-sm-12" );
        tableContainer.addStyleName( "col-sm-8 col-xs-7" );
        detailsContainer.removeStyleName( "no-width" );
        detailsContainer.addStyleName( "col-sm-4 col-xs-5" );
    }

    public void closeDetails() {
        detailsContainer.clear();
        tableContainer.removeStyleName( "col-sm-8" );
        tableContainer.removeStyleName( "col-xs-7" );
        tableContainer.addStyleName( "col-sm-12" );
        detailsContainer.removeStyleName( "col-sm-4" );
        detailsContainer.removeStyleName( "col-xs-5" );
        detailsContainer.addStyleName( "no-width" );
    }

    public void setContainers( HTMLPanel tableContainer, HTMLPanel detailsContainer) {
        this.tableContainer = tableContainer;
        this.detailsContainer = detailsContainer;
    }

    private HTMLPanel tableContainer;
    private HTMLPanel detailsContainer;
}
