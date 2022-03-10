package ru.protei.portal.ui.delivery.client.activity.cardbatch.common;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.PersonProjectMemberView;

import java.util.Set;

public interface AbstractCardBatchCommonInfoEditView extends IsWidget {

    HasVisibility buttonsContainerVisibility();

    HasValue<String> number();

    HasValue<Integer> amount();

    void setAmountValid(boolean isValid);

    HasValue<String> params();

    HasValue<Set<PersonProjectMemberView>> contractors();

    boolean isNumberValid();

    void hidePrevCardBatchInfo();

    void setPrevCardBatchInfo(String number, int amount, String state);

    void setActivity(AbstractCardBatchCommonInfoEditActivity activity);

    HasEnabled saveEnabled();

    void hideNumberContainer();
}
