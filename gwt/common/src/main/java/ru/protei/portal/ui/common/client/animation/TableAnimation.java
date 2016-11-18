package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Анимация базовой таблицы с карточкой
 */
public class TableAnimation {

    public void showDetails() {
        tableContainer.removeStyleName( "col-xs-9" );
        tableContainer.addStyleName( "col-xs-4" );

        detailsContainer.removeStyleName( "no-width" );
        detailsContainer.addStyleName( "col-xs-8" );

        filterContainer.removeStyleName( "col-xs-3" );
        filterContainer.addStyleName( "no-width" );
    }

    public void closeDetails() {
        detailsContainer.clear();

        tableContainer.removeStyleName( "col-xs-4" );
        tableContainer.addStyleName( "col-xs-9" );

        detailsContainer.removeStyleName( "col-xs-8" );
        detailsContainer.addStyleName( "no-width" );

        filterContainer.removeStyleName( "no-width" );
        filterContainer.addStyleName( "col-xs-3" );
    }

/*    public void showFilter() {
        tableContainer.removeStyleName( "col-xs-12" );
        tableContainer.addStyleName( "col-xs-8" );
        filterContainer.removeStyleName( "no-width" );
        filterContainer.addStyleName( "col-xs-4" );
    }

    public void closeFilter() {
        detailsContainer.clear();
        tableContainer.removeStyleName( "col-xs-8" );
        tableContainer.addStyleName( "col-xs-12" );
        detailsContainer.removeStyleName( "col-xs-4" );
        detailsContainer.addStyleName( "no-width" );
    }*/

    public void setContainers( HTMLPanel tableContainer, HTMLPanel detailsContainer,  HTMLPanel filterContainer) {
        this.tableContainer = tableContainer;
        this.detailsContainer = detailsContainer;
        this.filterContainer = filterContainer;
    }

    private HTMLPanel tableContainer;
    private HTMLPanel detailsContainer;
    private HTMLPanel filterContainer;
}
