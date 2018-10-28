package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Анимация базовой таблицы с карточкой
 */
public class TableAnimation {

    public void showDetails() {
        tableContainer.removeStyleName( "col-md-9" );
        tableContainer.removeStyleName( "filter-collapsed" );
        tableContainer.addStyleName( "col-md-5 short-table-view" );

        detailsContainer.removeStyleName( "no-width" );
        detailsContainer.addStyleName( "col-md-7" );

        filterContainer.removeStyleName( "col-md-3" );
        filterContainer.removeStyleName( "collapsed" );
        filterContainer.addStyleName( "no-display" );
    }

    public void closeDetails() {
        detailsContainer.clear();

        tableContainer.removeStyleName( "col-md-5" );
        tableContainer.removeStyleName( "short-table-view" );

        tableContainer.addStyleName( "col-md-9" );

        detailsContainer.removeStyleName( "col-md-7" );
        detailsContainer.addStyleName( "no-width" );

        filterContainer.removeStyleName( "no-display" );
        filterContainer.addStyleName( filterCollapsed ? "collapsed" : "col-md-3" );
    }

    public void filterCollapse() {
        filterCollapsed = true;
        tableContainer.removeStyleName( "col-md-9" );
        tableContainer.addStyleName( "filter-collapsed" );
        filterContainer.removeStyleName( "col-md-3" );
        filterContainer.addStyleName( "collapsed" );
    }

    public void filterRestore() {
        filterCollapsed = false;
        tableContainer.removeStyleName( "filter-collapsed" );
        tableContainer.addStyleName( "col-md-9" );
        filterContainer.removeStyleName( "collapsed" );
        filterContainer.addStyleName( "col-md-3" );
    }

    public void setContainers( HTMLPanel tableContainer, HTMLPanel detailsContainer,  HTMLPanel filterContainer) {
        this.tableContainer = tableContainer;
        this.detailsContainer = detailsContainer;
        this.filterContainer = filterContainer;
    }

    private HTMLPanel tableContainer;
    private HTMLPanel detailsContainer;
    private HTMLPanel filterContainer;
    private boolean filterCollapsed = false;
}
