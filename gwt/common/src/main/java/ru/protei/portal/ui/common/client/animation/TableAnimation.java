package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Анимация базовой таблицы с карточкой
 */
public class TableAnimation {

    public void showDetails() {
        tableContainer.removeStyleName( "col-xs-9" );
        tableContainer.addStyleName( "col-xs-5 short-table-view" );

        detailsContainer.removeStyleName( "no-width" );
        detailsContainer.addStyleName( "col-xs-7" );

        filterContainer.removeStyleName( "col-xs-3" );
        filterContainer.addStyleName( "no-display" );
    }

    public void closeDetails() {
        detailsContainer.clear();

        tableContainer.removeStyleName( "col-xs-5 short-table-view" );
        tableContainer.addStyleName( "col-xs-9" );

        detailsContainer.removeStyleName( "col-xs-7" );
        detailsContainer.addStyleName( "no-width" );

        filterContainer.removeStyleName( "no-display" );
        filterContainer.addStyleName( "col-xs-3" );
    }

    public void setContainers( HTMLPanel tableContainer, HTMLPanel detailsContainer,  HTMLPanel filterContainer) {
        this.tableContainer = tableContainer;
        this.detailsContainer = detailsContainer;
        this.filterContainer = filterContainer;
    }

    private HTMLPanel tableContainer;
    private HTMLPanel detailsContainer;
    private HTMLPanel filterContainer;
}
