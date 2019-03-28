package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import java.util.function.Function;

/**
 * Колонока с динамическим html контентом
 */
public class DynamicColumn<T> extends ClickColumn<T>{

    public DynamicColumn(String headerName, String className, Function<T, String> valueGenerator) {
        this.headerName = headerName;
        this.className = className;
        this.valueGenerator = valueGenerator;
    }
    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        if ( className != null ) {
            columnHeader.addClassName(className);
        }
        columnHeader.setInnerText( headerName );
    }

    @Override
    public void fillColumnValue( Element cell, T value ) {
        if ( className != null ) {
            cell.addClassName(className);
        }

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();
        divElement.setInnerHTML( valueGenerator.apply(value) );

        cell.appendChild( divElement );
    }

    private String headerName;
    private String className;
    private Function<T, String> valueGenerator;

}
