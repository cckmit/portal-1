package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.dndautoresizetextarea.DndAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginModel;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginSelector;
import ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable.ArrowSelectableSelectorHandler;
import ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable.ArrowSelectableSelectorPopup;

public class MentioningTextArea extends DndAutoResizeTextArea implements ArrowSelectableSelectorHandler {
    @Inject
    public MentioningTextArea(UserLoginModel userLoginModel,
                              UserLoginSelector userLoginSelector) {

        this.userLoginModel = userLoginModel;
        this.userLoginSelector = userLoginSelector;
        this.changeTimer = initTimer(userLoginModel, userLoginSelector);

        SelectorPopup selectorPopup
                = new ArrowSelectableSelectorPopup(KeyCodes.KEY_ENTER, true, this);

        selectorPopup.addStyleName("user-login-selector");

        initUserLoginSelector(userLoginModel, userLoginSelector, selectorPopup);

        addKeyDownHandler(event -> onKeyDown(event, selectorPopup));
        addDomHandler(event -> onInput(changeTimer), InputEvent.getType());
        addClickHandler(event -> changeTimer.run());
    }

    @Override
    public void onBlurSelector() {
        getElement().focus();
    }

    public void setPersonId(Long personId) {
        userLoginModel.setPersonFirstId(personId);
    }

    public void setInitiatorCompanyId(Long initiatorCompanyId) {
        userLoginModel.setInitiatorCompanyId(initiatorCompanyId);
    }

    public void setIsMentionEnabled(Boolean isMentionEnabled) {
        this.isMentionEnabled = isMentionEnabled;
    }

    private void onInput(Timer changeTimer) {
        if (!isMentionEnabled) {
            return;
        }

        changeTimer.schedule(200);
    }

    private void onKeyDown(KeyDownEvent event, SelectorPopup selectorPopup) {

        if (!isMentionEnabled) {
            return;
        }

        if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
            event.preventDefault();
            selectorPopup.focusPopup();
        }
    }

    private void initUserLoginSelector(final UserLoginModel userLoginModel,
                                       final UserLoginSelector userLoginSelector,
                                       SelectorPopup selectorPopup) {

        userLoginSelector.setPopup(selectorPopup);
        userLoginSelector.setAsyncSearchModel(userLoginModel);
        userLoginSelector.setRelative(getElement(), true);
        userLoginSelector.setSearchEnabled(false);
        userLoginSelector.addValueChangeHandler(event -> {
            int cursorPosition = cursorPosition(getElement());

            String beforeReplace = getValue().substring(0, possibleLoginInfo.index);
            String afterReplace = getValue().substring(cursorPosition);

            setValue(beforeReplace + userLoginSelector.getValue().getUlogin() + " " + afterReplace);

            getElement().focus();
        });
    }

    private Timer initTimer(final UserLoginModel userLoginModel, final UserLoginSelector userLoginSelector) {
        return new Timer() {
            @Override
            public void run() {
                PossibleLoginInfo possibleLoginInfo = possibleLogin(cursorPosition(getElement()));

                if (possibleLoginInfo == null) {
                    hidePopup(userLoginSelector);
                    return;
                }

                userLoginModel.setSearchString(possibleLoginInfo.possibleLogin);
                showPopup(userLoginSelector);

                MentioningTextArea.this.possibleLoginInfo = possibleLoginInfo;
            }
        };
    }

    private PossibleLoginInfo possibleLogin(int cursorPosition) {
        String substring = getValue().substring(0, cursorPosition);

        int spaceEnterPosition = Math.max(substring.lastIndexOf(' '), substring.lastIndexOf('\n'));
        int roundBracketsPosition = Math.max(substring.lastIndexOf('('), substring.lastIndexOf(')'));
        int squareBracketsPosition = Math.max(substring.lastIndexOf('['), substring.lastIndexOf(']'));

        int desiredPosition = Math.max(spaceEnterPosition, Math.max(roundBracketsPosition, squareBracketsPosition));

        final int possibleAtPosition = desiredPosition + 1; // "at" means "@"

        if (possibleAtPosition >= substring.length()) {
            return null;
        }

        if (substring.charAt(possibleAtPosition) != '@') {
            return null;
        }

        int loginStartPosition = possibleAtPosition + 1; // without "@" at the beginning

        String possibleLogin = getValue().substring(loginStartPosition, cursorPosition);

        return new PossibleLoginInfo(possibleLogin, loginStartPosition);
    }

    private void showPopup(UserLoginSelector userLoginSelector) {
        userLoginSelector.showPopup();
        userLoginSelector.clearAndFill();
    }

    private void hidePopup(UserLoginSelector userLoginSelector) {
        userLoginSelector.clearPopup();
        userLoginSelector.hidePopup();
    }

    private native int cursorPosition(Element textArea) /*-{
        return textArea.selectionStart;
    }-*/;

    private final UserLoginModel userLoginModel;
    private final UserLoginSelector userLoginSelector;
    private final Timer changeTimer;
    private boolean isMentionEnabled = true;

    private PossibleLoginInfo possibleLoginInfo;

    private static final class PossibleLoginInfo {
        private final String possibleLogin;
        private final int index;

        PossibleLoginInfo(String possibleLogin, int index) {
            this.possibleLogin = possibleLogin;
            this.index = index;
        }
    }
}
