package ru.protei.portal.ui.crm.client.activity.locale.list;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItemImage;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;
import ru.protei.portal.ui.crm.client.activity.locale.LocaleImagesHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Активность списка языков приложения
 */
public abstract class LocaleMenuActivity implements
        Activity,
        ClickHandler {

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        fillLanguageMap( LocaleInfo.getAvailableLocaleNames() );
    }

    @Event
    public void onShowLanguageSelector( AppEvents.ShowLocaleList event ) {
        fillList( event.parent );
    }

    @Override
    public void onClick( ClickEvent event ) {
        LocaleImagesHelper.LocaleModel model = itemViewToModel.get( event.getSource() );
        if ( model == null ) {
            return;
        }

        History.fireCurrentHistoryState();
        changeLang( model.lang );
    }

    private void fillList( IsWidget parent ) {
        itemViewToModel.clear();
        sp.getChildContainer().clear();

        for ( LocaleImagesHelper.LocaleModel model : langModelList ) {
            SelectorItemImage itemView = itemFactory.get();
            itemViewToModel.put( itemView, model );
            itemView.addClickHandler( this );

            itemView.setImage( model.icon );
            itemView.setName( model.name );
            sp.getChildContainer().add( itemView );
        }
        sp.showNear( parent );
    }

    private void fillLanguageMap( String[] languages ) {
        for ( String language : languages ) {
            LocaleImagesHelper.LocaleModel model = LocaleImagesHelper.getModel( language );
            if ( model == null ) {
                continue;
            }
            langModelList.add( model );
        }
    }

    private void changeLang( String language ) {
        String href = Window.Location.getHref();
        String newHref;

        if ( !href.contains( "locale" ) ) {
            if ( href.contains( "#" ) ) {
                newHref = href.replace( "#", "?locale=" + language + "#" );
            } else {
                newHref = href + "?locale=" + language;
            }
        } else {
            String locale = LocaleInfo.getCurrentLocale().getLocaleName();
            if ( locale.isEmpty() )
                newHref = href.replace( "#", "?locale=" + language + "#" );
            else {
                String replace = "locale=" + locale;
                newHref = href.replace( replace, "locale=" + language );
            }
        }
        Window.Location.replace( newHref );
    }


    @Inject
    Provider< SelectorItemImage > itemFactory;

    SelectorPopup sp = new SelectorPopup();
    List< LocaleImagesHelper.LocaleModel > langModelList = new ArrayList<>();
    Map< SelectorItemImage, LocaleImagesHelper.LocaleModel > itemViewToModel = new HashMap<>();
}
