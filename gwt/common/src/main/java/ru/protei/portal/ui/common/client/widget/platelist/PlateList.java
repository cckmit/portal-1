package ru.protei.portal.ui.common.client.widget.platelist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.platelist.events.AddEvent;
import ru.protei.portal.ui.common.client.widget.platelist.events.AddHandler;
import ru.protei.portal.ui.common.client.widget.platelist.events.HasAddHandlers;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Вид селектора
 */
public class PlateList extends Composite implements HasWidgets, HasAddHandlers {

    public PlateList() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @UiHandler( "createButton" )
    public void createButtonClick( ClickEvent event ) {
        AddEvent.fire( this );
    }

    @Override
    public void add( Widget widget ) {
        root.add( widget );
    }

    @Override
    public void clear() {
        root.clear();
        root.add( createButton );
    }

    @Override
    public Iterator<Widget> iterator() {
        return root.iterator();
    }

    @Override
    public boolean remove( Widget widget ) {
        return root.remove( widget );
    }

    @Override
    public void forEach( Consumer<? super Widget> action ) {
        root.forEach( action );
    }

    @Override
    public Spliterator<Widget> spliterator() {
        return root.spliterator();
    }

    @Override
    public HandlerRegistration addAddHandler( AddHandler handler ) {
        return addHandler( handler, AddEvent.getType() );
    }

    public void setCreateButtonStyleName( String value ) {
        createButton.setStyleName( value );
    }

    public void setCreateButtonCaption( String value ) {
        name.setHTML( value );
    }

    @UiField
    FocusPanel createButton;
    @UiField
    HTMLPanel root;
    @UiField
    HTML name;

    interface InputSelectorUiBinder extends UiBinder<HTMLPanel, PlateList> { }
    private static InputSelectorUiBinder ourUiBinder = GWT.create(InputSelectorUiBinder.class);

}