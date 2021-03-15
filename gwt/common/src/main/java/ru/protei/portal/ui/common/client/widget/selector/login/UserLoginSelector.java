package ru.protei.portal.ui.common.client.widget.selector.login;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupUserLoginSelectorItem;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.widget.popupselector.PopupSingleSelector;

import java.util.Iterator;

import static ru.protei.portal.core.model.util.CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE;

public class UserLoginSelector extends PopupSingleSelector<UserLoginShortView> implements HasVisibility {
    public UserLoginSelector() {
        setPageSize(DEFAULT_SELECTOR_PAGE_SIZE);
    }

    public void clearAndFill() {
        getPopup().clear();
        fill();
    }

    @Override
    public void onLoadingComplete() {
        super.onLoadingComplete();

        if (getPopup().isEmpty()) {
            getPopup().hide();
        }
    }

    @Override
    protected SelectorItem<UserLoginShortView> makeSelectorItem( UserLoginShortView element, String elementHtml ) {
        PopupUserLoginSelectorItem<UserLoginShortView> popupSelectorItem = new PopupUserLoginSelectorItem<>();
        popupSelectorItem.setLogin(element.getUlogin());
        popupSelectorItem.setUserName(element.getLastName() + " " + element.getFirstName());
        popupSelectorItem.setImage(AvatarUtils.getAvatarUrl(element.getPersonId(), element.getCompanyCategory(), En_Gender.parse(element.getGenderCode())));
        return popupSelectorItem;
    }
}
