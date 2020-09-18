package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.widget.dndautoresizetextarea.DndAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginModel;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginSelector;

import static java.util.Optional.ofNullable;

public class MentioningTextArea extends DndAutoResizeTextArea {
    @Inject
    public MentioningTextArea(UserLoginModel userLoginModel, UserLoginSelector userLoginSelector) {
        this.userLoginModel = userLoginModel;

        initUserLoginSelector(userLoginModel, userLoginSelector);
        final Timer changeTimer = initTimer(userLoginModel, userLoginSelector);

        addKeyUpHandler(event -> changeTimer.schedule(200));
        addClickHandler(event -> changeTimer.run());
    }

    public void setPersonId(Long personId) {
        userLoginModel.setPersonFirstId(personId);
    }

    private void initUserLoginSelector(final UserLoginModel userLoginModel, final UserLoginSelector userLoginSelector) {
        userLoginSelector.setAsyncSearchModel(userLoginModel);
        userLoginSelector.setRelative(getElement());
        userLoginSelector.addValueChangeHandler(event -> {
            int pointerPosition = pointerPosition(getElement());

            String beforeReplace = getValue().substring(0, possibleLoginInfo.index);
            String afterReplace = getValue().substring(pointerPosition);

            setValue(beforeReplace + userLoginSelector.getValue().getUlogin() + " " + afterReplace);

            userLoginSelector.getPopup().hide();

            getElement().focus();
        });
    }

    private Timer initTimer(final UserLoginModel userLoginModel, final UserLoginSelector userLoginSelector) {
        return new Timer() {
            @Override
            public void run() {
                PossibleLoginInfo possibleLoginInfo = possibleLogin(pointerPosition(getElement()));

                if (possibleLoginInfo == null) {
                    userLoginSelector.getPopup().getChildContainer().clear();
                    userLoginSelector.getPopup().hide();

                    return;
                }

                updateModel(possibleLoginInfo.possibleLogin, userLoginModel);
                showPopup(userLoginSelector);

                MentioningTextArea.this.possibleLoginInfo = possibleLoginInfo;
            }
        };
    }

    private PossibleLoginInfo possibleLogin(int pointerPosition) {
        String substring = getValue().substring(0, pointerPosition);
        int spacePosition = substring.lastIndexOf(' ');
        int enterPosition = substring.lastIndexOf('\n');
        final int atPosition = Math.max(spacePosition, enterPosition) + 1; // "at" means "@"

        String possibleMention = getValue().substring(atPosition, pointerPosition);

        return ofNullable(MENTION_REGEXP.exec(possibleMention))
                .map(matchResult -> matchResult.getGroup(0).equals(possibleMention) ? matchResult : null)
                .map(matchResult -> new PossibleLoginInfo(matchResult.getGroup(0).substring(1), atPosition + 1))
                .orElse(null);
    }

    private void showPopup(UserLoginSelector userLoginSelector) {
        userLoginSelector.getPopup().showNear(getElement());
        userLoginSelector.clearAndFill();
    }

    private void updateModel(String searchString, UserLoginModel userLoginModel) {
        userLoginModel.setSearchString(searchString);
    }

    private native int pointerPosition(Element textArea) /*-{
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
    private static final Integer COMMENT_BORDER_SIZE = 1;
    private static final Integer COMMENT_INNER_PADDING = 12;
}
