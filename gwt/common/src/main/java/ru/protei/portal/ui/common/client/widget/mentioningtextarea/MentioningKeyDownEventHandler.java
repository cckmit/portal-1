package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginSelector;

import java.util.*;
import java.util.function.Supplier;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

class MentioningKeyDownEventHandler {
    KeyDownHandler getTextAreaKeyDownHandler(final UserLoginSelector userLoginSelector,
                                             final Timer changeTimer,
                                             final Supplier<Iterator<Widget>> userLoginIteratorSupplier) {

        return event -> {
            if (!userLoginSelector.isPopupVisible()) {
                changeTimer.schedule(200);
                return;
            }

            if (event.getNativeKeyCode() != KeyCodes.KEY_DOWN) {
                changeTimer.schedule(200);
                return;
            }

            fillWidgets(userLoginIteratorSupplier.get(), userLoginItems, userLoginItemToIndex);

            if (isEmpty(userLoginItems)) {
                return;
            }

            event.preventDefault();
            focusUserLoginItem(getNext(userLoginItems));
        };
    }

    KeyDownHandler getPopupKeyDownHandler(final TextArea textArea,
                                          final Supplier<Iterator<Widget>> userLoginIteratorSupplier) {

        return event -> {
            if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
                event.preventDefault();
                focusTextArea(textArea);
                return;
            }

            if (KeyCodes.KEY_UP == event.getNativeKeyCode()) {
                onKeyUpClicked(event, userLoginItems, textArea);
                return;
            }

            if (KeyCodes.KEY_DOWN == event.getNativeKeyCode()) {
                onKeyDownClicked(event, userLoginItems, userLoginItemToIndex, userLoginIteratorSupplier);
                return;
            }
        };
    }

    private void onKeyUpClicked(KeyDownEvent event, List<Widget> userLoginItems, TextArea textArea) {
        event.preventDefault();
        Widget previousUserLoginItem = getPrevious(userLoginItems);

        if (previousUserLoginItem == null) {
            focusTextArea(textArea);
            return;
        }

        focusUserLoginItem(previousUserLoginItem);
    }

    private void onKeyDownClicked(KeyDownEvent event,
                                  List<Widget> userLoginItems,
                                  Map<Widget, Integer> userLoginItemToIndex,
                                  Supplier<Iterator<Widget>> userLoginIteratorSupplier) {

        event.preventDefault();
        Widget nextUserLoginItem = getNext(userLoginItems);

        if (nextUserLoginItem == null) {
            fillWidgets(userLoginIteratorSupplier.get(), userLoginItems, userLoginItemToIndex);
        }

        focusUserLoginItem(getNext(userLoginItems));
    }

    private Widget getNext(List<Widget> userLoginItems) {
        if (isEmpty(userLoginItems)) {
            return null;
        }

        Integer nextIndex = Optional
                .ofNullable(focusedUserLoginItem)
                .map(userLoginItemToIndex::get)
                .map(currentIndex -> currentIndex + 1)
                .orElse(null);

        if (nextIndex == null) {
            return CollectionUtils.getFirst(userLoginItems);
        }

        if (nextIndex >= userLoginItems.size()) {
            return null;
        }

        return userLoginItems.get(nextIndex);
    }

    private Widget getPrevious(List<Widget> userLoginItems) {
        if (isEmpty(userLoginItems)) {
            return null;
        }

        Integer previousIndex = Optional
                .ofNullable(focusedUserLoginItem)
                .map(userLoginItemToIndex::get)
                .map(currentIndex -> currentIndex - 1)
                .orElse(null);

        if (previousIndex == null) {
            return CollectionUtils.getFirst(userLoginItems);
        }

        if (previousIndex < 0) {
            return null;
        }

        return userLoginItems.get(previousIndex);
    }

    private void fillWidgets(Iterator<Widget> userLoginIterator,
                             List<Widget> userLoginItems,
                             Map<Widget, Integer> userLoginItemToIndex) {

        if (userLoginIterator == null) {
            return;
        }

        userLoginItems.clear();
        userLoginItemToIndex.clear();

        int index = 0;

        while (userLoginIterator.hasNext()) {
            Widget next = userLoginIterator.next();
            userLoginItemToIndex.put(next, index++);
            next.addDomHandler(event -> resetFocusedUserLoginItem(), MouseOverEvent.getType());
            userLoginItems.add(next);
        }
    }

    private void focusTextArea(TextArea textArea) {
        focusedUserLoginItem = null;
        textArea.getElement().focus();
    }

    private void focusUserLoginItem(Widget userLoginItem) {
        if (userLoginItem == null) {
            return;
        }

        focusedUserLoginItem = userLoginItem;
        userLoginItem.getElement().getFirstChildElement().focus();
    }

    private void resetFocusedUserLoginItem() {
        if (focusedUserLoginItem == null) {
            return;
        }

        focusedUserLoginItem.getElement().getFirstChildElement().blur();
        focusedUserLoginItem = null;
    }

    private List<Widget> userLoginItems = new ArrayList<>();
    private Map<Widget, Integer> userLoginItemToIndex = new HashMap<>();
    private Widget focusedUserLoginItem;
}
