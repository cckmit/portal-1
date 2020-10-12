package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.widget.dndautoresizetextarea.DndAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginModel;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginSelector;

import static java.util.Optional.ofNullable;

public class MentioningTextArea extends DndAutoResizeTextArea {
    @Inject
    public MentioningTextArea(UserLoginModel userLoginModel,
                              UserLoginSelector userLoginSelector) {

        this.userLoginModel = userLoginModel;

        initUserLoginSelector(userLoginModel, userLoginSelector);
        final Timer changeTimer = initTimer(userLoginModel, userLoginSelector);

        initKeyDownSelector(new PopupWithTextAreaKeyDownEventHandler(this, userLoginSelector.getPopup().getChildContainer()), userLoginSelector, changeTimer);
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

                updateModel(possibleLoginInfo.possibleLogin, userLoginModel);
                showPopup(userLoginSelector);

                MentioningTextArea.this.possibleLoginInfo = possibleLoginInfo;
            }
        };
    }

    private void initKeyDownSelector(PopupWithTextAreaKeyDownEventHandler popupWithTextAreaKeyDownEventHandler, UserLoginSelector userLoginSelector, Timer changeTimer) {
        Runnable onChange = () -> changeTimer.schedule(200);

        addKeyDownHandler(
                popupWithTextAreaKeyDownEventHandler.getTextAreaKeyDownHandler(userLoginSelector::isPopupVisible, onChange)
        );

        userLoginSelector.getPopup().addKeyDownHandler(
                popupWithTextAreaKeyDownEventHandler.getPopupKeyDownHandler(this, onChange)
        );
    }

    private PossibleLoginInfo possibleLogin(int pointerPosition) {
        String substring = getValue().substring(0, pointerPosition);

        int spaceEnterPosition = Math.max(substring.lastIndexOf(' '), substring.lastIndexOf('\n'));
        int roundBracketsPosition = Math.max(substring.lastIndexOf('('), substring.lastIndexOf(')'));
        int squareBracketsPosition = Math.max(substring.lastIndexOf('['), substring.lastIndexOf(']'));

        int desiredPosition = Math.max(spaceEnterPosition, Math.max(roundBracketsPosition, squareBracketsPosition));

        final int possibleAtPosition = desiredPosition + 1; // "at" means "@"

        String possibleMention = getValue().substring(possibleAtPosition, pointerPosition);

        return ofNullable(MENTION_REGEXP.exec(possibleMention))
                .map(matchResult -> new PossibleLoginInfo(matchResult.getGroup(0).substring(1), possibleAtPosition + 1))
                .orElse(null);
    }

    private void showPopup(UserLoginSelector userLoginSelector) {
        userLoginSelector.showPopup();
        userLoginSelector.clearAndFill();
    }

    private void hidePopup(UserLoginSelector userLoginSelector) {
        userLoginSelector.clearPopup();
        userLoginSelector.hidePopup();
    }

    private void updateModel(String searchString, UserLoginModel userLoginModel) {
        userLoginModel.setSearchString(searchString);
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

    private static final RegExp MENTION_REGEXP = RegExp.compile(CrmConstants.Masks.MENTION);
}
