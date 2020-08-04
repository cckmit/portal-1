package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Icons.*;

public class FavoritesClickColumn<T> extends ClickColumn<T> {
    public interface FavoritesStateManager<T> {
        boolean isFavoriteItem(T value);
        void onFavoriteStateChanged(T value);
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
        favoriteElement.addClassName((isFavoriteItem(value) ? FAVORITE_ACTIVE : FAVORITE_NOT_ACTIVE));
        favoriteElement.addClassName(BIG_ICON);
        favoriteElement.setTitle(isFavoriteItem(value) ? lang.issueRemoveFromFavorites() : lang.issueAddToFavorites());
        favoriteElement.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.BUTTON.FAVORITES);
        cell.appendChild(favoriteElement);
        cell.addClassName("favorites");

        DOM.sinkEvents(favoriteElement, Event.ONCLICK);
        DOM.setEventListener(favoriteElement, event -> {
            event.preventDefault();
            if (favoritesStateManager != null) {
                favoritesStateManager.onFavoriteStateChanged(value);
            }
        });
    }

    public void setFavoritesStateManager(FavoritesStateManager<T> favoritesStateManager) {
        this.favoritesStateManager = favoritesStateManager;
    }

    private boolean isFavoriteItem(T value) {
        return favoritesStateManager != null && favoritesStateManager.isFavoriteItem(value);
    }

    private FavoritesStateManager<T> favoritesStateManager;
    private Lang lang;
}
