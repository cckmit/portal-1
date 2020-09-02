package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.dndautoresizetextarea.DndAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginModel;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginSelector;

import static java.util.Optional.ofNullable;

public class MentioningTextArea extends DndAutoResizeTextArea {
    @Inject
    public void init() {
        initUserLoginSelector();
        addKeyUpHandler(event -> changeTimer.schedule(200));
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        userLoginSelector.getPopup().getChildContainer().clear();
        userLoginSelector.getPopup().hide();
    }

    private void initUserLoginSelector() {
        userLoginSelector.setAsyncSearchModel(userLoginModel);
        userLoginSelector.setRelative(this);
        userLoginSelector.addValueChangeHandler(event -> {
            int pointerPosition = pointerPosition(getElement());

            String beforeReplace = getValue().substring(0, possibleLoginInfo.index);
            String afterReplace = getValue().substring(pointerPosition);

            setValue(beforeReplace + userLoginSelector.getValue().getUlogin() + " " + afterReplace);

            userLoginSelector.getPopup().hide();

            getElement().focus();
        });
    }

    private PossibleLoginInfo possibleLogin(int pointerPosition) {
        String substring = getValue().substring(0, pointerPosition);
        int spacePosition = substring.lastIndexOf(' ');
        int enterPosition = substring.lastIndexOf('\n');
        final int atPosition = Math.max(spacePosition, enterPosition) + 1;

        String possibleMention = getValue().substring(atPosition, pointerPosition);

        return ofNullable(MENTION_REGEXP.exec(possibleMention))
                .map(matchResult -> matchResult.getGroup(0).equals(possibleMention) ? matchResult : null)
                .map(matchResult -> new PossibleLoginInfo(matchResult.getGroup(0).substring(1), atPosition + 1))
                .orElse(null);
    }

    private void showPopup(String searchString) {
        userLoginModel.setSearchString(searchString);
        userLoginSelector.clearAndFill();
    }

    private native int pointerPosition(Element textArea) /*-{
        return textArea.selectionStart;
    }-*/;

    @Inject
    UserLoginModel userLoginModel;

    @Inject
    UserLoginSelector userLoginSelector;

    private Timer changeTimer = new Timer() {
        @Override
        public void run() {
            PossibleLoginInfo possibleLoginInfo = possibleLogin(pointerPosition(getElement()));

            if (possibleLoginInfo == null) {
                userLoginSelector.getPopup().getChildContainer().clear();
                userLoginSelector.getPopup().hide();

                return;
            }

            userLoginSelector.getPopup().showNear(MentioningTextArea.this);

            MentioningTextArea.this.possibleLoginInfo = possibleLoginInfo;

            showPopup(possibleLoginInfo.possibleLogin);
        }
    };

    private PossibleLoginInfo possibleLoginInfo;

    private static final RegExp MENTION_REGEXP = RegExp.compile("^\\@(\\w|@)*");

    private static final class PossibleLoginInfo {
        private String possibleLogin;
        private int index;

        PossibleLoginInfo(String possibleLogin, int index) {
            this.possibleLogin = possibleLogin;
            this.index = index;
        }
    }
}
