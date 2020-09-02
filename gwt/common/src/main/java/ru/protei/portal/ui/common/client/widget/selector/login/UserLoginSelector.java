package ru.protei.portal.ui.common.client.widget.selector.login;

import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupUserLoginSelectorItem;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.widget.popupselector.PopupSingleSelector;

import static ru.protei.portal.core.model.util.CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE;

public class UserLoginSelector extends PopupSingleSelector<UserLoginShortView> {
    public UserLoginSelector() {
        setPageSize(DEFAULT_SELECTOR_PAGE_SIZE);
        setSearchEnabled(false);
        getPopup().addStyleName("user-login-selector");
    }

    public void clearAndFill() {
        getPopup().getChildContainer().clear();
        fill();
    }

    @Override
    protected SelectorItem<UserLoginShortView> makeSelectorItem(UserLoginShortView element, String elementHtml) {
        PopupUserLoginSelectorItem<UserLoginShortView> popupSelectorItem = new PopupUserLoginSelectorItem<>();
        popupSelectorItem.setLogin(element.getUlogin());
        popupSelectorItem.setUserName(element.getLastName() + " " + element.getFirstName());
        popupSelectorItem.setImage(AvatarUtils.getAvatarUrl(element.getPersonId(), element.getCompanyCategory(), element.getGender()));
        return popupSelectorItem;
    }
}
