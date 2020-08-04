package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class FavoritesClickColumn<T> extends ClickColumn<T> {
    public interface FavoritesStateManager<T> {
        boolean isFavoriteActive(T value);
        void changeFavoriteState(T value);
    }

    public FavoritesClickColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("favorites");
    }

    @Override
    protected void fillColumnValue(Element cell, T value) {
        AnchorElement favoriteElement = DOM.createAnchor().cast();
        favoriteElement.setHref("#");
        favoriteElement.addClassName(favoritesStateManager.isFavoriteActive(value) ? ACTIVE_STAR : NOT_ACTIVE_STAR);
        favoriteElement.setTitle(!favoritesStateManager.isFavoriteActive(value) ? lang.issueAddToFavorites() : lang.issueRemoveFromFavorites());
        favoriteElement.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.FAVORITES);
        cell.appendChild(favoriteElement);
        cell.addClassName("favorites");

        DOM.sinkEvents(favoriteElement, Event.ONCLICK);
        DOM.setEventListener( favoriteElement, event -> {
            event.preventDefault();
            if ( favoritesStateManager != null ) {
                favoritesStateManager.changeFavoriteState(value);
            }
        });
    }

    public void setFavoritesStateManager(FavoritesStateManager<T> favoritesStateManager) {
        this.favoritesStateManager = favoritesStateManager;
    }

    private FavoritesStateManager<T> favoritesStateManager;
    private Lang lang;

    private static final String ACTIVE_STAR = "fas fa-star fa-lg";
    private static final String NOT_ACTIVE_STAR = "far fa-star fa-lg";
}
