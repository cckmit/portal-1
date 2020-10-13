package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.widget.dndautoresizetextarea.DndAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginModel;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginSelector;

public class MentioningTextArea extends DndAutoResizeTextArea {
    @Inject
    public MentioningTextArea(UserLoginModel userLoginModel,
                              UserLoginSelector userLoginSelector) {

        this.userLoginModel = userLoginModel;

        initUserLoginSelector(userLoginModel, userLoginSelector);
        final Timer changeTimer = initTimer(userLoginModel, userLoginSelector);

        initKeyDownHandler(
                this,
                userLoginSelector.getPopup().getChildContainerAsComplexPanel(),
                changeTimer
        );

        addClickHandler(event -> changeTimer.run());
    }

    public void setPersonId(Long personId) {
        userLoginModel.setPersonFirstId(personId);
    }

    private void initUserLoginSelector(final UserLoginModel userLoginModel, final UserLoginSelector userLoginSelector) {
        userLoginSelector.setAsyncSearchModel(userLoginModel);
        userLoginSelector.setRelative(getElement(), true);
        userLoginSelector.addValueChangeHandler(event -> {
            int cursorPosition = cursorPosition(getElement());

            String beforeReplace = getValue().substring(0, possibleLoginInfo.index);
            String afterReplace = getValue().substring(cursorPosition);

            setValue(beforeReplace + userLoginSelector.getValue().getUlogin() + " " + afterReplace);

            hidePopup(userLoginSelector);

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

    private void initKeyDownHandler(MentioningTextArea textArea, ComplexPanel childContainer, Timer changeTimer) {
        PopupWithTextAreaKeyDownEventHandler popupWithTextAreaKeyDownEventHandler
                = new PopupWithTextAreaKeyDownEventHandler(textArea, childContainer);

        popupWithTextAreaKeyDownEventHandler.setDefaultKeyDownHandler(event -> changeTimer.schedule(200));
    }

    private PossibleLoginInfo possibleLogin(int cursorPosition) {
        String substring = getValue().substring(0, cursorPosition);

        if (StringUtils.isBlank(substring)) {
            return null;
        }

        int spaceEnterPosition = Math.max(substring.lastIndexOf(' '), substring.lastIndexOf('\n'));
        int roundBracketsPosition = Math.max(substring.lastIndexOf('('), substring.lastIndexOf(')'));
        int squareBracketsPosition = Math.max(substring.lastIndexOf('['), substring.lastIndexOf(']'));

        int desiredPosition = Math.max(spaceEnterPosition, Math.max(roundBracketsPosition, squareBracketsPosition));

        final int possibleAtPosition = desiredPosition + 1; // "at" means "@"

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
