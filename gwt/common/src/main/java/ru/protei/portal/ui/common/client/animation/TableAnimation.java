package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Анимация базовой таблицы с карточкой
 */
public class TableAnimation {

    public void showDetails() {
        tableContainer.removeStyleName( tableStyleWithoutDetails );
        tableContainer.removeStyleName( "filter-collapsed" );
        tableContainer.addStyleName( styleTableWithDetails );
        tableContainer.addStyleName( "short-table-view" );

        detailsContainer.removeStyleName( "no-width" );
        detailsContainer.addStyleName( styleDetails );

        if ( noFilter ) return;

        filterContainer.removeStyleName( styleFilter );
        filterContainer.removeStyleName( "collapsed" );
        filterContainer.addStyleName( "no-display" );
    }

    public void closeDetails() {
        detailsContainer.clear();

        tableContainer.removeStyleName( styleTableWithDetails );
        tableContainer.removeStyleName( "short-table-view" );

        tableContainer.addStyleName( tableStyleWithoutDetails );

        detailsContainer.removeStyleName( styleDetails );
        detailsContainer.addStyleName( "no-width" );

        if ( noFilter ) return;
        filterContainer.removeStyleName( "no-display" );
        filterContainer.addStyleName( filterCollapsed ? "collapsed" : styleFilter );
    }

    public void filterCollapse() {
        filterCollapsed = true;
        tableContainer.removeStyleName( tableStyleWithoutDetails );
        tableContainer.addStyleName( "filter-collapsed" );

        if ( noFilter ) return;

        filterContainer.removeStyleName( styleFilter );
        filterContainer.addStyleName( "collapsed" );
    }

    public void filterRestore() {
        filterCollapsed = false;
        tableContainer.removeStyleName( "filter-collapsed" );
        tableContainer.addStyleName( tableStyleWithoutDetails );

        if ( noFilter ) return;
        filterContainer.removeStyleName( "collapsed" );
        filterContainer.addStyleName( styleFilter );
    }

    public void setContainers( HTMLPanel tableContainer, HTMLPanel detailsContainer,  HTMLPanel filterContainer) {
        this.tableContainer = tableContainer;
        this.detailsContainer = detailsContainer;
        this.filterContainer = filterContainer;
        this.noFilter = filterContainer == null;
        applyTableStyleNoFilter();
    }

    public void setStyles(String styleTableFull, String styleTableWithFilter, String styleFilter, String styleTableWithDetails, String styleDetails) {
        this.styleTableFull = styleTableFull;
        this.styleTableWithFilter = styleTableWithFilter;
        this.styleFilter = styleFilter;
        this.styleTableWithDetails = styleTableWithDetails;
        this.styleDetails = styleDetails;
        applyTableStyleNoFilter();
    }

    private void applyTableStyleNoFilter() {
        if (noFilter) {
            tableStyleWithoutDetails = styleTableFull;
        } else {
            tableStyleWithoutDetails = styleTableWithFilter;
        }
    }

    private String styleTableFull = "col-md-12";
    private String styleTableWithFilter = "col-md-9";
    private String styleFilter = "col-md-3";
    private String styleTableWithDetails = "col-md-5";
    private String styleDetails = "col-md-7";

    private HTMLPanel tableContainer;
    private HTMLPanel detailsContainer;
    private HTMLPanel filterContainer;
    private boolean filterCollapsed = false;

    private boolean noFilter = false;
    private String tableStyleWithoutDetails;
}
